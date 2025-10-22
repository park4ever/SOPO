package com.sopo.repository.memberCoupon;

import com.sopo.domain.coupon.MemberCoupon;
import com.sopo.repository.memberCoupon.cond.MemberCouponQueryCond;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MemberCouponRepositoryCustom {

    Optional<MemberCoupon> findUsableByIdAndMember(Long memberCouponId, Long memberId, LocalDateTime now);

    List<Long> findIdsToExpire(LocalDateTime now, int limit);
    boolean existsIssuedByMemberAndCoupon(Long memberId, Long couponId);

    Page<MemberCoupon> search(MemberCouponQueryCond cond, Pageable pageable, LocalDateTime now);

    default Page<MemberCoupon> searchMyCoupons(Long memberId, MemberCouponQueryCond cond,
                                               Pageable pageable, LocalDateTime now) {
        MemberCouponQueryCond effective = (cond == null)
                ? MemberCouponQueryCond.builder().memberId(memberId).build()
                : cond.toBuilder().memberId(memberId).build();
        return search(effective, pageable, now);
    }
}