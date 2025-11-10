package com.sopo.dto.payment.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PaymentCancelRequest(
        @NotNull Long version,
        @Nullable @Size(max = 255) String reason
) {}