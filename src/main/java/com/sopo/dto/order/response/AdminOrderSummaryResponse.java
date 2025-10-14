package com.sopo.dto.order.response;

import com.sopo.domain.order.OrderStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record AdminOrderSummaryResponse(
        Long id,
        OffsetDateTime createdAt,
        OrderStatus status,
        String buyerName,
        String buyerEmail,
        int itemCount,
        BigDecimal totalPrice
) {}