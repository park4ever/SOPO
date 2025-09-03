package com.sopo.dto.cart.response;

import java.math.BigDecimal;
import java.util.List;

public record CartSummaryResponse(
        Long cartId,
        Long memberId,
        List<CartItemResponse> items,
        int totalQuantity,      //quantity
        BigDecimal subtotal,    //lineTotal
        boolean hasUnavailableItems
) {}