package com.sopo.dto.review.response;

public record ReviewSummaryResponse(
        long reviewCount,
        Double averageRating
) {}