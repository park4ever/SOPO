package com.sopo.dto.coupon.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PreviewMemberCouponRequest(
        @NotNull @DecimalMin("0.00")BigDecimal orderPrice
    ) { }