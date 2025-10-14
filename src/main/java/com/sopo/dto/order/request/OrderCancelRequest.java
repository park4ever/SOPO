package com.sopo.dto.order.request;

import jakarta.annotation.Nullable;

public record OrderCancelRequest(@Nullable String reason) {}