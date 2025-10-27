package com.sopo.dto.coupon.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CouponSummaryResponse(
        Long id,
        String name,
        String discountType,            //"FIXED", "RATE"
        BigDecimal fixedAmount,         //"FIXED"일 때만 값
        Integer percentage,             //"RATE"일 때만 값
        BigDecimal maxDiscountAmount,
        BigDecimal minOrderPrice,
        LocalDateTime validFrom,
        LocalDateTime validUntil,
        boolean active                  //now 기준 계산값
) { }