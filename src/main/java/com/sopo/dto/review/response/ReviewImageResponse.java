package com.sopo.dto.review.response;

public record ReviewImageResponse(
        Long id,
        String imageUrl,
        int sortOrder,
        boolean thumbnail
) {}