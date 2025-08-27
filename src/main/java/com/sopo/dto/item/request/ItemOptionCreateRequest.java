package com.sopo.dto.item.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ItemOptionCreateRequest(
        @NotNull Long colorId,
        @NotNull Long sizeId,
        @Min(0) int stock
) {}