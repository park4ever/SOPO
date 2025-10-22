package com.sopo.repository.order.cond;

import org.springframework.data.domain.Sort;

public record OrderSortSpec(OrderSortKey key, Sort.Direction dir) {}