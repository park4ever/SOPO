package com.sopo.domain.order;

public enum OrderStatus {
    ORDERED,      // 주문 접수
    PAID,         // 결제 완료
    PREPARING,    // 상품 준비 중
    SHIPPED,      // 배송 시작
    DELIVERED,    // 배송 완료
    CANCELED,     // 주문 취소
    RETURNED,     // 반품
    REFUNDED;      // 환불

    public boolean canTransitionTo(OrderStatus to) {
        if (this == to) return true;
        return switch (this) {
            case ORDERED -> (to == PAID || to == CANCELED);
            case PAID -> (to == PREPARING || to == CANCELED);
            case PREPARING -> (to == SHIPPED || to == CANCELED);
            case SHIPPED -> (to == DELIVERED);
            case DELIVERED -> (to == RETURNED);
            case RETURNED -> (to == REFUNDED);
            case CANCELED, REFUNDED -> false;
        };
    }

    public boolean isUserCancelable() {
        return this == ORDERED || this == PAID || this == PREPARING;
    }
}