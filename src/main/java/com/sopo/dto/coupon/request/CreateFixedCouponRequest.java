package com.sopo.dto.coupon.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateFixedCouponRequest(
        @NotBlank String name,
        @NotNull @DecimalMin(value = "0.01") BigDecimal fixedAmount,
        @NotNull @DecimalMin(value = "0.00") BigDecimal minOrderPrice,
        @NotNull LocalDateTime validFrom,
        @NotNull LocalDateTime validUntil
    ) { }