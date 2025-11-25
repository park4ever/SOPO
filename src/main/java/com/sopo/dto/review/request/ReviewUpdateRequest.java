package com.sopo.dto.review.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ReviewUpdateRequest(
        @Size(min = 1, max = 1000) String content,
        @Min(1) @Max(5) Integer rating,
        List<String> imageUrls,
        Long version
) {}