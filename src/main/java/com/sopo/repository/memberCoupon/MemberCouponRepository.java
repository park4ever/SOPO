package com.sopo.repository.memberCoupon;

import com.sopo.domain.coupon.MemberCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberCouponRepository extends JpaRepository<MemberCoupon, Long>, MemberCouponRepositoryCustom {

    Optional<MemberCoupon> findByIdAndMemberId(Long id, Long memberId);
}