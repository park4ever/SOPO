package com.sopo.dto.order.response;

import com.sopo.domain.order.OrderStatus;
import com.sopo.dto.order.response.view.OrderLineView;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record OrderDetailResponse(
        Long id,
        OffsetDateTime createdAt,
        OrderStatus status,
        BigDecimal totalPrice,
        List<OrderLineView> lines
) {}