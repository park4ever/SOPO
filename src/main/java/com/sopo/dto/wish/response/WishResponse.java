package com.sopo.dto.wish.response;

import com.sopo.domain.item.ItemStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WishResponse(
        Long id,
        Long itemId,
        String itemName,
        String brand,
        BigDecimal price,
        ItemStatus status,
        String thumbnailUrl,
        LocalDateTime createdDate
) {}