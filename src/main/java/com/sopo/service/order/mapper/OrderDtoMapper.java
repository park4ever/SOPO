package com.sopo.service.order.mapper;

import com.sopo.domain.item.Item;
import com.sopo.domain.item.ItemOption;
import com.sopo.domain.order.Order;
import com.sopo.domain.order.OrderItem;
import com.sopo.dto.order.response.AdminOrderDetailResponse;
import com.sopo.dto.order.response.AdminOrderSummaryResponse;
import com.sopo.dto.order.response.OrderDetailResponse;
import com.sopo.dto.order.response.OrderSummaryResponse;
import com.sopo.dto.order.response.view.OrderLineView;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

public final class OrderDtoMapper {

    private static final ZoneId DEFAULT_ZONE = ZoneId.of("Asia/Seoul");

    private OrderDtoMapper() {}

    public static OrderSummaryResponse toSummary(Order order) {
        return new OrderSummaryResponse(
                order.getId(),
                toOffset(order.getCreatedDate()),
                order.getStatus(),
                order.getOrderItems().size(),
                order.getTotalPrice()
        );
    }

    public static OrderDetailResponse toDetail(Order order) {
        List<OrderLineView> lines = order.getOrderItems().stream()
                .map(OrderDtoMapper::toLineView)
                .toList();
        return new OrderDetailResponse(
                order.getId(),
                toOffset(order.getCreatedDate()),
                order.getStatus(),
                order.getTotalPrice(),
                lines
        );
    }

    public static AdminOrderSummaryResponse toAdminSummary(Order order) {
        return new AdminOrderSummaryResponse(
                order.getId(),
                toOffset(order.getCreatedDate()),
                order.getStatus(),
                order.getMember().getName(),
                order.getMember().getEmail(),
                order.getOrderItems().size(),
                order.getTotalPrice()
        );
    }

    public static AdminOrderDetailResponse toAdminDetail(Order order) {
        List<OrderLineView> lines = order.getOrderItems().stream()
                .map(OrderDtoMapper::toLineView)
                .toList();
        return new AdminOrderDetailResponse(
                order.getId(),
                toOffset(order.getCreatedDate()),
                order.getStatus(),
                order.getMember().getName(),
                order.getMember().getEmail(),
                order.getTotalPrice(),
                lines
        );
    }

    private static OrderLineView toLineView(OrderItem oi) {
        ItemOption opt = oi.getItemOption();
        Item item = opt.getItem();
        return new OrderLineView(
                opt.getId(),
                item.getId(),
                item.getName(),
                item.getBrand(),
                opt.getColor() != null ? opt.getColor().getName() : null,
                opt.getSize() != null ? opt.getSize().getName() : null,
                oi.getQuantity(),
                oi.getPrice(),
                oi.getTotalPrice()
        );
    }

    private static OffsetDateTime toOffset(LocalDateTime t) {
        return t.atZone(DEFAULT_ZONE).toOffsetDateTime();
    }
}