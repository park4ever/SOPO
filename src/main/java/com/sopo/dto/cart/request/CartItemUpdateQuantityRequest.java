package com.sopo.dto.cart.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CartItemUpdateQuantityRequest(
        @NotNull Long cartItemId,
        @NotNull @Positive Integer quantity
) {}