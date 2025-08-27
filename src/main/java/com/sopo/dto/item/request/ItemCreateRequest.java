package com.sopo.dto.item.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ItemCreateRequest(
        @NotBlank @Size(max = 50) String name,
        @Size(max = 10_000) String description,
        @NotNull @DecimalMin(value = "0.00", inclusive = false) BigDecimal price,
        @NotBlank @Size(max = 30) String brand,
        @NotNull Long sellerId,
        Long categoryId
) {}