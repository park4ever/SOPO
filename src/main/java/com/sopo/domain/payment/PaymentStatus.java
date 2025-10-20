package com.sopo.domain.payment;

public enum PaymentStatus {
    READY,         // 결제 준비됨
    IN_PROGRESS,   // 결제 처리 중
    COMPLETED,     // 결제 성공
    FAILED,        // 결제 실패
    CANCELED      // 결제 취소
}