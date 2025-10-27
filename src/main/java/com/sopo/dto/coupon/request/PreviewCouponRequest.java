package com.sopo.dto.coupon.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PreviewCouponRequest(
        @NotNull @DecimalMin(value = "0.00")BigDecimal orderPrice
    ) { }