package com.sopo.dto.order.response.view;

import java.math.BigDecimal;

public record OrderLineView(
        Long optionId,
        Long itemId,
        String itemName,
        String brand,
        String color,
        String size,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal
) {}