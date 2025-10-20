package com.sopo.domain.coupon;

import com.sopo.domain.common.BaseEntity;
import com.sopo.exception.BusinessException;
import com.sopo.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import static com.sopo.domain.coupon.DiscountType.*;
import static jakarta.persistence.EnumType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

@Entity
@Getter
@Table(name = "coupon")
@NoArgsConstructor(access = PROTECTED)
@SQLDelete(sql = "UPDATE coupon SET is_deleted = true WHERE coupon_id = ?")
@SQLRestriction("is_deleted = false")
public class Coupon extends BaseEntity {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "coupon_id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(STRING)
    @Column(nullable = false)
    private DiscountType discountType;

    @Column(precision = 15, scale = 2)
    private BigDecimal fixedAmount;

    @Column
    private Integer percentage;

    @Column(precision = 15, scale = 2)
    private BigDecimal maxDiscountAmount;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal minOrderPrice;

    @Column(nullable = false)
    private LocalDateTime validFrom;

    @Column(nullable = false)
    private LocalDateTime validUntil;

    @Column(nullable = false, name = "is_deleted")
    private boolean isDeleted;

    private Coupon(String name, DiscountType discountType, BigDecimal fixedAmount, Integer percentage,
                  BigDecimal maxDiscountAmount, BigDecimal minOrderPrice,
                  LocalDateTime validFrom, LocalDateTime validUntil) {
        this.name = requireName(name);
        this.discountType = discountType;

        if (discountType == FIXED) {
            this.fixedAmount = positiveMoney(fixedAmount, ErrorCode.COUPON_INVALID_AMOUNT);
            this.percentage = null;
            this.maxDiscountAmount = null;
        } else {    //RATE
            this.percentage = validPercentage(percentage);
            this.fixedAmount = null;

            BigDecimal cap = normalizeMoneyNullable(maxDiscountAmount);
            if (cap != null && cap.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException(ErrorCode.COUPON_INVALID_AMOUNT);
            }
            this.maxDiscountAmount = cap;
        }

        this.minOrderPrice = nonNegativeMoney(minOrderPrice, ErrorCode.COUPON_INVALID_AMOUNT);

        this.validFrom = requirePeriod(validFrom, validUntil);
        this.validUntil = validUntil;

        this.isDeleted = false;
    }

    // -------------------- 생성자 + 정적 팩토리 --------------------

    public static Coupon createFixed(String name, BigDecimal fixedAmount, BigDecimal minOrderPrice,
                                     LocalDateTime validFrom, LocalDateTime validUntil) {
        return new Coupon(name, FIXED, fixedAmount, null, null, minOrderPrice, validFrom, validUntil);
    }

    public static Coupon createRate(String name, Integer percentage, BigDecimal maxDiscountAmount, BigDecimal minOrderPrice,
                                    LocalDateTime validFrom, LocalDateTime validUntil) {
        return new Coupon(name, RATE, null, percentage, maxDiscountAmount, minOrderPrice, validFrom, validUntil);
    }

    // -------------------- 도메인 규칙 --------------------

    public boolean isActiveAt(LocalDateTime now) {
        return !isDeleted
                && (now.isAfter(validFrom) || now.isEqual(validFrom))
                && (now.isBefore(validUntil) || now.isEqual(validUntil));
    }

    public boolean isAvailableFor(BigDecimal orderPrice, LocalDateTime now) {
        return isActiveAt(now) && gte(normalizeMoney(orderPrice), minOrderPrice);
    }

    public BigDecimal calculateDiscount(BigDecimal orderPrice) {
        BigDecimal normalized = normalizeMoney(orderPrice);
        if (discountType == FIXED) {
            return min(fixedAmount, normalized);
        } else {
            int p = percentage;
            BigDecimal computed = normalized
                    .multiply(BigDecimal.valueOf(p))
                    .divide(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
            if (maxDiscountAmount != null) {
                computed = min(computed, maxDiscountAmount);
            }
            return min(computed, normalized);
        }
    }

    public BigDecimal previewDiscountIfAvailable(BigDecimal orderPrice, LocalDateTime now) {
        return isAvailableFor(orderPrice, now)
                ? calculateDiscount(orderPrice)
                : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    public void delete() {
        this.isDeleted = true;
    }

    // -------------------- 내뷰 유틸/검증 메서드 --------------------

    private static String requireName(String name) {
        if (name == null || name.isBlank()) throw new BusinessException(ErrorCode.INVALID_PARAM);
        return name.trim();
    }

    private static LocalDateTime requirePeriod(LocalDateTime from, LocalDateTime until) {
        if (from == null || until == null) throw new BusinessException(ErrorCode.COUPON_INVALID_PERIOD);
        if (from.isAfter(until)) throw new BusinessException(ErrorCode.COUPON_INVALID_PERIOD);
        return from;
    }

    private static Integer validPercentage(Integer percentage) {
        if (percentage == null) throw new BusinessException(ErrorCode.COUPON_INVALID_PERCENTAGE);
        if (percentage < 1 || percentage > 100) throw new BusinessException(ErrorCode.COUPON_INVALID_PERCENTAGE);
        return percentage;
    }

    private static BigDecimal positiveMoney(BigDecimal v, ErrorCode code) {
        BigDecimal n = normalizeMoney(v);
        if (n.compareTo(BigDecimal.ZERO) <= 0) throw new BusinessException(code);
        return n;
    }

    private static BigDecimal nonNegativeMoney(BigDecimal v, ErrorCode code) {
        BigDecimal n = normalizeMoney(v);
        if (n.compareTo(BigDecimal.ZERO) < 0) throw new BusinessException(code);
        return n;
    }

    private static BigDecimal normalizeMoney(BigDecimal v) {
        if (v == null) throw new BusinessException(ErrorCode.INVALID_PARAM);
        return v.setScale(2, RoundingMode.HALF_UP);
    }

    private static BigDecimal normalizeMoneyNullable(BigDecimal v) {
        return (v == null) ? null : v.setScale(2, RoundingMode.HALF_UP);
    }

    private static boolean gte(BigDecimal a, BigDecimal b) {
        return a.compareTo(b) >= 0;
    }

    private static BigDecimal min(BigDecimal a, BigDecimal b) {
        return (a.compareTo(b) <= 0) ? a : b;
    }
}