package com.sopo.dto.item.request;

import com.sopo.domain.item.ItemStatus;
import jakarta.validation.constraints.NotNull;

public record ItemStatusChangeRequest(
        @NotNull ItemStatus status
) {}