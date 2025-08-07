package com.sopo.domain.coupon;

import com.sopo.common.BaseEntity;
import com.sopo.domain.member.Member;
import com.sopo.domain.order.Order;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(nullable = false)
    private boolean used;

    @Column(nullable = false)
    private LocalDateTime issuedAt;

    private MemberCoupon(Member member, Coupon coupon) {
        this.member = member;
        this.coupon = coupon;
        this.used = false;
        this.issuedAt = LocalDateTime.now();
    }

    public static MemberCoupon issueTo(Member member, Coupon coupon) {
        return new MemberCoupon(member, coupon);
    }

    public boolean isExpired() {
        return coupon.isExpired();
    }

    public boolean canBeUsed(BigDecimal orderPrice) {
        return !used && !isExpired() && coupon.isAvailable(orderPrice);
    }

    public void markUsed(Order order) {
        this.used = true;
        this.order = order;
    }
}
