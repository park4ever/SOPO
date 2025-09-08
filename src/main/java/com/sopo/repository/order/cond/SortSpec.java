package com.sopo.repository.order.cond;

import lombok.Builder;
import lombok.Getter;

import static org.springframework.data.domain.Sort.*;

@Getter
@Builder
public class SortSpec {

    private final OrderSortKey key;
    private final Direction dir;
}