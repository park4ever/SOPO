package com.sopo.dto.cart.response;

import java.math.BigDecimal;

public record CartItemResponse(
        Long cartItemId,
        Long itemId,
        String itemName,
        String brand,
        Long itemOptionId,
        String optionColor,
        String optionSize,
        BigDecimal unitPrice,       //Item.price
        int quantity,
        BigDecimal lineTotal,       //unitPrice * quantity
        boolean available,          //구매 가능 여부
        Integer maxPurchasableQty,  //null이면 제한 X, 아니면 재고 상한
        String thumbnailUrl         //없으면 null
) {}