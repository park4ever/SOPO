package com.sopo.dto.wish.request;

import jakarta.validation.constraints.NotNull;

public record WishCreateRequest(
        @NotNull Long itemId
) {}