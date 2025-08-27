package com.sopo.dto.item.response;

import com.sopo.domain.item.ItemStatus;

import java.math.BigDecimal;

public record ItemDetailResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        String brand,
        ItemStatus status,
        Long sellerId,
        Long categoryId,
        boolean deleted,
        int salesVolume
) {}