package com.sopo.controller.payment.admin;

import com.sopo.config.auth.LoginMember;
import com.sopo.dto.payment.request.PaymentApproveRequest;
import com.sopo.dto.payment.request.PaymentCancelRequest;
import com.sopo.dto.payment.request.PaymentCreateRequest;
import com.sopo.dto.payment.response.PaymentResponse;
import com.sopo.security.session.MemberSession;
import com.sopo.service.payment.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PaymentApiController {

    private final PaymentService paymentService;

    @PostMapping("/payments")
    public ResponseEntity<Long> create(@LoginMember MemberSession session,
                                       @Valid @RequestBody PaymentCreateRequest request) {
        Long id = paymentService.create(session.id(), request);
        return ResponseEntity.created(URI.create("/api/payments/" + id))
                .body(id);
    }

    @PostMapping("/payments/{id}/approve")
    public ResponseEntity<Void> approve(@LoginMember MemberSession session,
                                        @PathVariable("id") Long paymentId,
                                        @Valid @RequestBody PaymentApproveRequest request) {
        paymentService.approve(session.id(), paymentId, request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/payments/{id}/cancel")
    public ResponseEntity<Void> cancel(@LoginMember MemberSession session,
                                       @PathVariable("id") Long paymentId,
                                       @Valid @RequestBody PaymentCancelRequest request) {
        paymentService.cancel(session.id(), paymentId, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/orders/{orderId}/payment")
    public PaymentResponse getByOrder(@LoginMember MemberSession session,
                                      @PathVariable("orderId") Long orderId) {
        return paymentService.getByOrderId(session.id(), orderId);
    }
}