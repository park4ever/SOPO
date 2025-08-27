package com.sopo.dto.item.request;

public record ItemImageResponse(
        Long id,
        String imageUrl,
        boolean thumbnail,
        int sortOrder
) {}