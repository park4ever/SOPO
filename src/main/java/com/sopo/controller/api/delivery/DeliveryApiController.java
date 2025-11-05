package com.sopo.controller.api.delivery;

import com.sopo.config.auth.LoginMember;
import com.sopo.dto.delivery.request.DeliveryCreateRequest;
import com.sopo.dto.delivery.request.DeliveryUpdateRequest;
import com.sopo.dto.delivery.response.DeliveryResponse;
import com.sopo.security.session.MemberSession;
import com.sopo.service.delivery.DeliveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class DeliveryApiController {

    private final DeliveryService deliveryService;

    @PostMapping("/deliveries")
    public ResponseEntity<Long> create(@LoginMember MemberSession session,
                                       @RequestBody @Valid DeliveryCreateRequest request) {
        Long id = deliveryService.create(session.id(), request);
        return ResponseEntity.created(URI.create("/api/deliveries/" + id)).body(id);
    }

    @PatchMapping("/deliveries/{id}")
    public ResponseEntity<Void> update(@LoginMember MemberSession session,
                                       @PathVariable("id") Long deliveryId,
                                       @RequestBody @Valid DeliveryUpdateRequest request) {
        deliveryService.update(session.id(), deliveryId, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/orders/{orderId}/delivery")
    public DeliveryResponse getByOrder(@LoginMember MemberSession session,
                                       @PathVariable("orderId") Long orderId) {
        return deliveryService.getByOrderId(session.id(), orderId);
    }
}