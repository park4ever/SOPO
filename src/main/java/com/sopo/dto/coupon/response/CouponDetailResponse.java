package com.sopo.dto.coupon.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CouponDetailResponse(
        Long id,
        String name,
        String discountType,
        BigDecimal fixedAmount,
        Integer percentage,
        BigDecimal maxDiscountAmount,
        BigDecimal minOrderPrice,
        LocalDateTime validFrom,
        LocalDateTime validUntil,
        boolean deleted,
        boolean active
) { }