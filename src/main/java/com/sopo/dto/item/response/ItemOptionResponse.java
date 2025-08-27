package com.sopo.dto.item.response;

public record ItemOptionResponse(
        Long id,
        Long colorId,
        String colorName,
        Long sizeId,
        String sizeName,
        int stock,
        boolean soldOut
) {}