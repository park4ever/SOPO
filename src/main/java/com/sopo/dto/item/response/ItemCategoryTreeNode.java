package com.sopo.dto.item.response;

import java.util.List;

public record ItemCategoryTreeNode(
        Long id,
        String name,
        Integer depth,
        boolean deleted,
        List<ItemCategoryTreeNode> children
) {}