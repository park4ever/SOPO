package com.sopo.dto.item.request;

import java.util.List;

public record ItemImageReorderRequest(
        List<ImageOrder> orders
) {
    public record ImageOrder(Long imageId, int sortOrder) {}
}