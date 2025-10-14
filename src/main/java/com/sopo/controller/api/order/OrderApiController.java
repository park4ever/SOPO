package com.sopo.controller.api.order;

import com.sopo.config.auth.LoginMember;
import com.sopo.dto.order.request.OrderCreateRequest;
import com.sopo.dto.order.response.OrderDetailResponse;
import com.sopo.dto.order.response.OrderSummaryResponse;
import com.sopo.repository.order.cond.OrderQueryCond;
import com.sopo.security.session.MemberSession;
import com.sopo.service.order.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
@Validated
public class OrderApiController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Long> create(@LoginMember MemberSession session,
                                       @RequestBody @Valid OrderCreateRequest request) {
        Long orderId = orderService.create(session.id(), request);
        return ResponseEntity.ok(orderId);
    }

    @GetMapping("/{id}")
    public OrderDetailResponse get(@LoginMember MemberSession session,
                                   @PathVariable("id") Long orderId) {
        return orderService.getMyOrder(session.id(), orderId);
    }

    @GetMapping
    public Page<OrderSummaryResponse> list(@LoginMember MemberSession session,
                                           OrderQueryCond cond, Pageable pageable) {
        return orderService.searchMyOrders(session.id(), cond, pageable);
    }
}