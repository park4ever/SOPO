package com.sopo.repository.coupon.cond;

import org.springframework.data.domain.Sort;

public record CouponSortSpec(CouponSortKey key, Sort.Direction dir) {}