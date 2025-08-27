package com.sopo.dto.item.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ItemUpdateRequest(
        @Size(max = 50) String name,
        @Size(max = 10_000) String description,
        @DecimalMin(value = "0.00", inclusive = false)BigDecimal price,
        @Size(max = 30) String brand,
        Long categoryId
) {}