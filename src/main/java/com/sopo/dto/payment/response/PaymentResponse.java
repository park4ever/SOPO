package com.sopo.dto.payment.response;

import com.sopo.domain.payment.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
        Long id,
        Long orderId,
        String paymentKey,
        BigDecimal amount,
        String method,
        PaymentStatus status,
        Long version,
        LocalDateTime createdDate,
        LocalDateTime lastModifiedDate
) {}