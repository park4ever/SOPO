package com.sopo.service.payment;

import com.sopo.dto.payment.request.PaymentApproveRequest;
import com.sopo.dto.payment.request.PaymentCancelRequest;
import com.sopo.dto.payment.request.PaymentCreateRequest;
import com.sopo.dto.payment.response.PaymentResponse;

public interface PaymentService {

    Long create(Long memberId, PaymentCreateRequest request);

    void approve(Long memberId, Long paymentId, PaymentApproveRequest request);

    void cancel(Long memberId, Long paymentId, PaymentCancelRequest request);

    PaymentResponse getByOrderId(Long memberId, Long orderId);
}