package com.sopo.dto.coupon.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateRateCouponRequest(
        @NotBlank String name,
        @NotNull @Min(1) @Max(100) Integer percentage,
        @NotNull @DecimalMin(value = "0.01") BigDecimal maxDiscountAmount,
        @NotNull @DecimalMin(value = "0.00") BigDecimal minOrderPrice,
        @NotNull LocalDateTime validFrom,
        @NotNull LocalDateTime validUntil
    ) { }