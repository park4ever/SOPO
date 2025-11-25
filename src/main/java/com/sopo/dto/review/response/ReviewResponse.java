package com.sopo.dto.review.response;

import com.sopo.domain.item.ItemStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ReviewResponse(
        Long id,
        Long itemId,
        String itemName,
        String itemBrand,
        BigDecimal itemPrice,
        ItemStatus itemStatus,

        Long memberId,
        String memberName,

        String content,
        int rating,
        LocalDateTime createdDate,
        LocalDateTime lastModifiedDate,

        Long version,
        List<ReviewImageResponse> images
) {}