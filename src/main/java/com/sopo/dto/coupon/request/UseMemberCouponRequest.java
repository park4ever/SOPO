package com.sopo.dto.coupon.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UseMemberCouponRequest(
        @NotNull Long memberCouponId,
        @NotNull Long memberId,
        @NotNull Long orderId,
        @NotNull @DecimalMin("0.00")BigDecimal orderPrice
    ) { }