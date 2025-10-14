package com.sopo.service.order;

import com.sopo.domain.member.Member;
import com.sopo.domain.order.Order;
import com.sopo.domain.order.OrderStatus;
import com.sopo.dto.order.request.OrderCancelRequest;
import com.sopo.dto.order.request.OrderCreateRequest;
import com.sopo.dto.order.response.AdminOrderDetailResponse;
import com.sopo.dto.order.response.AdminOrderSummaryResponse;
import com.sopo.dto.order.response.OrderDetailResponse;
import com.sopo.dto.order.response.OrderSummaryResponse;
import com.sopo.exception.BusinessException;
import com.sopo.repository.member.MemberRepository;
import com.sopo.repository.order.OrderRepository;
import com.sopo.repository.order.cond.OrderQueryCond;
import com.sopo.security.aop.AdminOnly;
import com.sopo.service.order.mapper.OrderDtoMapper;
import com.sopo.service.order.processor.OrderPlacementProcessor;
import com.sopo.service.order.processor.OrderStockRestorer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.sopo.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final OrderPlacementProcessor placementProcessor;
    private final OrderStockRestorer stockRestorer;

    @Override
    public Long create(Long memberId, OrderCreateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(MEMBER_NOT_FOUND));

        var orderItems = placementProcessor.buildOrderItems(request.lines());

        Order order = Order.create(member, orderItems);
        orderRepository.save(order);

        return order.getId();
    }

    @Override
    public void cancel(Long memberId, Long orderId, OrderCancelRequest reason) {
        Order order = getMyOrderWithDetails(memberId, orderId);

        var current = order.getStatus();
        if (current == OrderStatus.CANCELED) throw new BusinessException(ORDER_ALREADY_CANCELED);
        if (!current.isUserCancelable()) throw new BusinessException(INVALID_ORDER_STATUS_CHANGE);

        order.changeStatus(OrderStatus.CANCELED);   //전이
        stockRestorer.restore(order);               //재고/판매량 복원
        //TODO : reason 보관 로직이 생기면 여기서 기록
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDetailResponse getMyOrder(Long memberId, Long orderId) {
        return OrderDtoMapper.toDetail(getMyOrderWithDetails(memberId, orderId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderSummaryResponse> searchMyOrders(Long memberId, OrderQueryCond cond, Pageable pageable) {
        return orderRepository.searchMyOrders(memberId, cond, pageable)
                .map(OrderDtoMapper::toSummary);
    }

    @Override
    @AdminOnly
    @Transactional(readOnly = true)
    public AdminOrderDetailResponse getByIdAsAdmin(Long orderId) {
        return OrderDtoMapper.toAdminDetail(getOrderWithDetails(orderId));
    }

    @Override
    @AdminOnly
    @Transactional(readOnly = true)
    public Page<AdminOrderSummaryResponse> searchOrdersAsAdmin(OrderQueryCond cond, Pageable pageable) {
        return orderRepository.searchOrders(cond == null ? OrderQueryCond.builder().build() : cond, pageable)
                .map(OrderDtoMapper::toAdminSummary);
    }

    @Override
    @AdminOnly
    public void updateStatusAsAdmin(Long orderId, OrderStatus targetStatus) {
        Order order = getOrderWithDetails(orderId);

        var current = order.getStatus();
        if (current == targetStatus) return;
        if (!current.canTransitionTo(targetStatus)) {
            throw new BusinessException(INVALID_ORDER_STATUS_CHANGE);
        }

        order.changeStatus(targetStatus);           //전이
        if (targetStatus == OrderStatus.CANCELED) { //필요 시 복원
            stockRestorer.restore(order);
        }
    }

    private Order getMyOrderWithDetails(Long memberId, Long orderId) {
        Order order = orderRepository.findWithDetailsByIdAndMemberId(orderId, memberId)
                .orElseThrow(() -> new BusinessException(ORDER_NOT_FOUND));
        if (order.isDeleted()) throw new BusinessException(ORDER_DELETED);
        return order;
    }

    private Order getOrderWithDetails(Long orderId) {
        return orderRepository.findWithDetailsById(orderId)
                .orElseThrow(() -> new BusinessException(ORDER_NOT_FOUND));
    }
}