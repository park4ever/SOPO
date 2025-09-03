package com.sopo.dto.cart.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CartItemAddRequest(
        @NotNull Long itemId,
        @NotNull Long itemOptionId,
        @NotNull @Positive Integer quantity
) {}