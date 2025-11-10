package com.sopo.dto.payment.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PaymentApproveRequest(
        @NotNull Long version,
        @NotNull @Positive @Digits(integer = 10, fraction = 2)BigDecimal approvedAmount
    ) { }