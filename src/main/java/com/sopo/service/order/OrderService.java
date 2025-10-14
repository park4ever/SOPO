package com.sopo.service.order;

import com.sopo.domain.order.OrderStatus;
import com.sopo.dto.order.request.OrderCancelRequest;
import com.sopo.dto.order.request.OrderCreateRequest;
import com.sopo.dto.order.response.AdminOrderDetailResponse;
import com.sopo.dto.order.response.AdminOrderSummaryResponse;
import com.sopo.dto.order.response.OrderDetailResponse;
import com.sopo.dto.order.response.OrderSummaryResponse;
import com.sopo.repository.order.cond.OrderQueryCond;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    /** USER 영역 */
    Long create(Long memberId, OrderCreateRequest request);

    void cancel(Long memberId, Long orderId, OrderCancelRequest reason);

    OrderDetailResponse getMyOrder(Long memberId, Long orderId);

    Page<OrderSummaryResponse> searchMyOrders(Long memberId, OrderQueryCond cond, Pageable pageable);

    /** ADMIN 영역 */
    AdminOrderDetailResponse getByIdAsAdmin(Long orderId);

    Page<AdminOrderSummaryResponse> searchOrdersAsAdmin(OrderQueryCond cond, Pageable pageable);

    void updateStatusAsAdmin(Long orderId, OrderStatus targetStatus);
}