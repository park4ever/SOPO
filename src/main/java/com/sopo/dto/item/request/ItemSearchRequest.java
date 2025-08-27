package com.sopo.dto.item.request;

import com.sopo.domain.item.ItemStatus;
import jakarta.validation.constraints.Min;

public record ItemSearchRequest(
        String keyword,
        Long categoryId,
        ItemStatus status,
        @Min(0) int page,
        @Min(1) int size,
        String sort
) {}