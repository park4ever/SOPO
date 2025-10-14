package com.sopo.controller.api.order.admin;

import com.sopo.domain.order.OrderStatus;
import com.sopo.dto.order.response.AdminOrderDetailResponse;
import com.sopo.dto.order.response.AdminOrderSummaryResponse;
import com.sopo.exception.BusinessException;
import com.sopo.exception.ErrorCode;
import com.sopo.repository.order.cond.OrderQueryCond;
import com.sopo.security.aop.AdminOnly;
import com.sopo.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/orders")
@AdminOnly
public class AdminOrderApiController {

    private final OrderService orderService;

    @GetMapping("/{id}")
    public AdminOrderDetailResponse get(@PathVariable("id") Long id) {
        return orderService.getByIdAsAdmin(id);
    }

    @GetMapping
    public Page<AdminOrderSummaryResponse> list(OrderQueryCond cond, Pageable pageable) {
        return orderService.searchOrdersAsAdmin(cond, pageable);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable("id") Long id,
                                             @RequestParam("target")OrderStatus target) {
        if (target == null) throw new BusinessException(ErrorCode.INVALID_PARAM);
        orderService.updateStatusAsAdmin(id, target);
        return ResponseEntity.noContent().build();
    }
}