package com.sopo.controller.api.cart;

import com.sopo.config.auth.LoginMember;
import com.sopo.dto.cart.request.CartItemAddRequest;
import com.sopo.dto.cart.request.CartItemUpdateQuantityRequest;
import com.sopo.dto.cart.response.CartSummaryResponse;
import com.sopo.exception.BusinessException;
import com.sopo.exception.ErrorCode;
import com.sopo.security.session.MemberSession;
import com.sopo.service.cart.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
@Validated
public class CartApiController {

    private final CartService cartService;

    @GetMapping
    public CartSummaryResponse get(@LoginMember MemberSession session) {
        return cartService.getMyCart(session.id());
    }

    @PostMapping("/items")
    public ResponseEntity<Long> add(@LoginMember MemberSession session,
                                    @RequestBody @Valid CartItemAddRequest request) {
        Long cartItemId = cartService.addItem(session.id(), request);
        return ResponseEntity.ok(cartItemId);
    }

    @PatchMapping("/items/{cartItemId}")
    public ResponseEntity<Void> update(@LoginMember MemberSession session,
                                       @PathVariable("cartItemId") Long cartItemId,
                                       @RequestBody @Valid CartItemUpdateQuantityRequest request) {
        //요청 본문과 경로변수 정합성 체크(의도 불일치 방지)
        if (!cartItemId.equals(request.cartItemId())) {
            throw new BusinessException(ErrorCode.INVALID_PARAM);
        }

        cartService.updateQuantity(session.id(), request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<Void> remove(@LoginMember MemberSession session,
                                       @PathVariable("cartItemId") Long cartItemId) {
        cartService.removeItem(session.id(), cartItemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clear(@LoginMember MemberSession session) {
        cartService.clear(session.id());
        return ResponseEntity.noContent().build();
    }
}