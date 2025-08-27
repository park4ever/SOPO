package com.sopo.dto.item.request;

import jakarta.validation.constraints.NotBlank;

public record ItemImageCreateRequest(
        @NotBlank String imageUrl,
        boolean thumbnail,
        Integer sortOrder
) {}