package com.sopo.service.cart;

import com.sopo.dto.cart.request.CartItemAddRequest;
import com.sopo.dto.cart.request.CartItemUpdateQuantityRequest;
import com.sopo.dto.cart.response.CartSummaryResponse;

public interface CartService {
    CartSummaryResponse getMyCart(Long memberId);

    Long addItem(Long memberId, CartItemAddRequest request);
    void updateQuantity(Long memberId, CartItemUpdateQuantityRequest request);

    void removeItem(Long memberId, Long cartItemId);
    void clear(Long memberId);
}