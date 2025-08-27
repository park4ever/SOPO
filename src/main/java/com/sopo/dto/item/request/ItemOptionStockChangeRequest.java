package com.sopo.dto.item.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ItemOptionStockChangeRequest(
        @NotNull Long optionId,
        @Min(1) int quantity
) {}