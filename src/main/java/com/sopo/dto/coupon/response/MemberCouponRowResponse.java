package com.sopo.dto.coupon.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MemberCouponRowResponse(
        Long id,
        String status,
        LocalDateTime issuedAt,
        LocalDateTime usedAt,
        String couponName,
        BigDecimal minOrderPrice,
        LocalDateTime validFrom,
        LocalDateTime validUntil
) { }