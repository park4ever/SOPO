package com.sopo.dto.delivery.response;

import java.time.LocalDateTime;

public record DeliveryResponse(
        Long id,
        Long orderId,
        Long addressId,
        String receiverName,
        String receiverPhone,
        Long version,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) { }