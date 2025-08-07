package com.sopo.domain.coupon;

import com.sopo.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

@Entity
@Getter
@Table(name = "coupon")
@NoArgsConstructor(access = PROTECTED)
public class Coupon extends BaseEntity {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "coupon_id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(STRING)
    @Column(nullable = false)
    private DiscountType discountType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal minOrderPrice;

    @Column(nullable = false)
    private LocalDateTime expirationDate;

    @Column(nullable = false, name = "is_deleted")
    private boolean isDeleted;

    private Coupon(String name, DiscountType discountType, BigDecimal discountValue,
                   BigDecimal minOrderPrice, LocalDateTime expirationDate) {
        this.name = name;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.minOrderPrice = minOrderPrice;
        this.expirationDate = expirationDate;
        this.isDeleted = false;
    }

    public static Coupon create(String name, DiscountType discountType, BigDecimal discountValue,
                                BigDecimal minOrderPrice, LocalDateTime expirationDate) {
        return new Coupon(name, discountType, discountValue, minOrderPrice, expirationDate);
    }

    public void markAsDeleted() {
        this.isDeleted = true;
    }
}
