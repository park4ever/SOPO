package com.sopo.dto.order.response;

import com.sopo.domain.order.OrderStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record OrderSummaryResponse(
        Long id,
        OffsetDateTime createdAt,
        OrderStatus status,
        int itemCount,
        BigDecimal totalPrice
) {}