package com.sopo.domain.coupon;

import com.sopo.domain.common.BaseEntity;
import com.sopo.domain.member.Member;
import com.sopo.domain.order.Order;
import com.sopo.exception.BusinessException;
import com.sopo.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.sopo.domain.coupon.MemberCouponStatus.*;
import static jakarta.persistence.EnumType.*;
import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Table(name = "member_coupon")
@NoArgsConstructor(access = PROTECTED)
public class MemberCoupon extends BaseEntity {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "member_coupon_id")
    private Long id;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "coupon_id",  nullable = false)
    private Coupon coupon;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Enumerated(STRING)
    @Column(nullable = false, length = 20)
    private MemberCouponStatus status;

    @Column(nullable = false)
    private LocalDateTime issuedAt;

    @Column
    private LocalDateTime usedAt;

    @Column
    private LocalDateTime canceledAt;

    @Version
    private Long version;

    private MemberCoupon(Member member, Coupon coupon, LocalDateTime now) {
        if (member == null || coupon == null) throw new BusinessException(ErrorCode.INVALID_PARAM);
        this.member = member;
        this.coupon = coupon;
        this.status = ISSUED;
        this.issuedAt = (now != null ? now : LocalDateTime.now());
    }

    public static MemberCoupon issueTo(Member member, Coupon coupon, LocalDateTime now) {
        return new MemberCoupon(member, coupon, now);
    }

    public boolean isExpired(LocalDateTime now) {
        return !coupon.isActiveAt(now);
    }

    public boolean canBeUsed(BigDecimal orderPrice, LocalDateTime now) {
        return status == ISSUED && coupon.isAvailableFor(orderPrice, now);
    }

    /**
     * 주문 사용 확정(서비스 트랜잭션 + @Version으로 중복 사용 방지)
     */
    public void markUsed(Order order, BigDecimal orderPrice, LocalDateTime now) {
        if (status != ISSUED) {
            throw new BusinessException(ErrorCode.MEMBER_COUPON_NOT_USABLE);
        }
        if (!coupon.isAvailableFor(orderPrice, now)) {
            throw new BusinessException(ErrorCode.COUPON_NOT_ACTIVE);
        }
        this.status = USED;
        this.order = order;
        this.usedAt = (now != null ? now : LocalDateTime.now());
    }

    /**
     * 주문 취소 등으로 사용 취소(원복)
     */
    public void markCanceled(LocalDateTime now) {
        if (status != USED) {
            throw new BusinessException(ErrorCode.MEMBER_COUPON_NOT_CANCELABLE);
        }
        this.status = CANCELED;
        this.canceledAt = (now != null ? now : LocalDateTime.now());
        this.order = null;
        this.usedAt = null;
    }

    /**
     * 기간 만료 처리(배치 훅)
     */
    public void expire(LocalDateTime now) {
        if (status == ISSUED || status == CANCELED) {
            this.status = EXPIRED;
        }
    }
}