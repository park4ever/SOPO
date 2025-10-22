package com.sopo.repository.memberCoupon.cond;

import org.springframework.data.domain.Sort;

public record MemberCouponSortSpec(MemberCouponSortKey key, Sort.Direction dir) {}