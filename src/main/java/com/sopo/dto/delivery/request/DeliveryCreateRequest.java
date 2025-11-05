package com.sopo.dto.delivery.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record DeliveryCreateRequest(
        @NotNull Long orderId,
        @NotNull Long addressId,
        @NotBlank @Size(max = 20) String receiverName,
        @NotBlank @Pattern(regexp = "\\d{10,11}") String receiverPhone
) {
    public DeliveryCreateRequest {
        if (receiverName != null) receiverName = receiverName.trim();
        if (receiverPhone != null) receiverPhone = receiverPhone.replaceAll("\\D", "");
    }
}