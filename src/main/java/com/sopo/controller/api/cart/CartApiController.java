package com.sopo.controller.api.cart;

import com.sopo.config.auth.LoginMember;
import com.sopo.dto.cart.request.CartItemAddRequest;
import com.sopo.dto.cart.request.CartItemUpdateQuantityRequest;
import com.sopo.dto.cart.response.CartSummaryResponse;
import com.sopo.exception.BusinessException;
import com.sopo.exception.ErrorCode;
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
    public CartSummaryResponse get(@LoginMember Long memberId) {
        return cartService.getMyCart(memberId);
    }

    @PostMapping("/items")
    public ResponseEntity<Long> add(@LoginMember Long memberId,
                                    @RequestBody @Valid CartItemAddRequest request) {
        Long cartItemId = cartService.addItem(memberId, request);
        return ResponseEntity.ok(cartItemId);
    }

    @PatchMapping("/items/{cartItemId}")
    public ResponseEntity<Void> update(@LoginMember Long memberId,
                                       @PathVariable("cartItemId") Long cartItemId,
                                       @RequestBody @Valid CartItemUpdateQuantityRequest request) {
        //요청 본문과 경로변수 정합성 체크(의도 불일치 방지)
        if (!cartItemId.equals(request.cartItemId())) {
            throw new BusinessException(ErrorCode.INVALID_PARAM);
        }

        cartService.updateQuantity(memberId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<Void> remove(@LoginMember Long memberId,
                                       @PathVariable("cartItemId") Long cartItemId) {
        cartService.removeItem(memberId, cartItemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clear(@LoginMember Long memberId) {
        cartService.clear(memberId);
        return ResponseEntity.noContent().build();
    }
}