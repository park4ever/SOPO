package com.sopo.dto.item.response;

public record ItemCategoryDetailResponse(
        Long id,
        String name,
        Long parentId,
        Integer depth,
        boolean deleted,
        Integer childrenCount
) {}