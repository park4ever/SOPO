package com.sopo.domain.order;

public enum OrderStatus {
    ORDERED,      // 주문 접수
    PAID,         // 결제 완료
    PREPARING,    // 상품 준비 중
    SHIPPED,      // 배송 시작
    DELIVERED,    // 배송 완료
    CANCELED,     // 주문 취소
    RETURNED,     // 반품
    REFUNDED      // 환불
}