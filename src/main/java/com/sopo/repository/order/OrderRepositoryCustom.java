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

    /** itemCount 전용 프로젝션 메서드
     *  기존 searchOrders(...)는 그대로 유지(여러 곳에서 재사용 가능).
     *  목록 화면에서만 쓰는 요약 전용 메서드를 리포지토리에 추가해,
     *  OrderSummaryRow 같은 가벼운 레코드로 바로 받아온다.
     * 혹은 엔티티 + 별도 "카운트 전용" 한 번의 그룹 쿼리 :
     *  현재 searchOrders 시그니처(Page<Order>)를 유지하고 싶다면,
     *  서비스에서 orderItems.size()를 건드리지 않도록, ID 집합에 대해 카운트만 한 번에 가져와 맵으로 사용.
     *  */
//    Page<OrderSummaryRow> searchOrderSummaries(OrderQueryCond cond, Pageable pageable);
}