package com.sopo.dto.order.response;

import com.sopo.domain.order.OrderStatus;
import com.sopo.dto.order.response.view.OrderLineView;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record AdminOrderDetailResponse(
        Long id,
        OffsetDateTime createdAt,
        OrderStatus status,
        String buyerName,
        String buyerEmail,
        BigDecimal totalPrice,
        List<OrderLineView> lines
) {}