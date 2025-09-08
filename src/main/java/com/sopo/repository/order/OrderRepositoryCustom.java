package com.sopo.repository.order;

import com.sopo.domain.order.Order;
import com.sopo.repository.order.cond.OrderQueryCond;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderRepositoryCustom {

    Page<Order> searchOrders(OrderQueryCond cond, Pageable pageable);

    /**
     * memberId를 강제 주입해, "내 주문" 검색으로 변환
     */
    default Page<Order> searchMyOrders(Long memberId, OrderQueryCond cond, Pageable pageable) {
        OrderQueryCond effective = (cond == null)
                ? OrderQueryCond.builder().memberId(memberId).build()
                : cond.toBuilder().memberId(memberId).build();
        return searchOrders(effective, pageable);
    }
}