package com.sopo.dto.item.response;

import com.sopo.domain.item.ItemStatus;

import java.math.BigDecimal;

public record ItemResponse(
        Long id,
        String name,
        BigDecimal price,
        String brand,
        ItemStatus status,
        Long categoryId,
        boolean deleted,
        int salesVolume
) {}