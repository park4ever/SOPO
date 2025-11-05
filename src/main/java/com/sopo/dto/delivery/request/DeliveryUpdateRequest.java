package com.sopo.dto.delivery.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record DeliveryUpdateRequest(
        @NotNull Long version,
        @Nullable @Size(max = 20) String receiverName,
        @Nullable @Pattern(regexp = "\\d{10,11}") String receiverPhone,
        @Nullable Long addressId
) {
    @AssertTrue(message = "수정할 값이 최소 1개 이상 필요합니다.")
    public boolean hasAnyUpdatableField() {
        return receiverName != null || receiverPhone == null || addressId != null;
    }

    public DeliveryUpdateRequest {
        if (receiverName != null) receiverName = receiverName.trim();
        if (receiverPhone != null) receiverPhone = receiverPhone.replaceAll("\\D", "");
    }
}