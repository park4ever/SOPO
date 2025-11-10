package com.sopo.service.payment;

import com.sopo.domain.order.Order;
import com.sopo.domain.payment.Payment;
import com.sopo.domain.payment.PaymentStatus;
import com.sopo.dto.payment.request.PaymentApproveRequest;
import com.sopo.dto.payment.request.PaymentCancelRequest;
import com.sopo.dto.payment.request.PaymentCreateRequest;
import com.sopo.dto.payment.response.PaymentResponse;
import com.sopo.exception.BusinessException;
import com.sopo.exception.ErrorCode;
import com.sopo.repository.order.OrderRepository;
import com.sopo.repository.payment.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static com.sopo.domain.order.OrderStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Override
    public Long create(Long memberId, PaymentCreateRequest request) {
        //본인 주문인지 + 존재 검증
        Order order = orderRepository.findByIdAndMemberId(request.orderId(), memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        //주문 상태 검증 : 결제 가능한 상태인지
        if (order.getStatus() != ORDERED) {
            throw new BusinessException(ErrorCode.PAYMENT_NOT_ALLOWED_FOR_ORDER_STATUS);
        }
        //주문당 결제 중복 방지
        if (paymentRepository.existsByOrderId(order.getId())) {
            throw new BusinessException(ErrorCode.PAYMENT_ALREADY_EXISTS);
        }
        //paymentKey 중복 방지
        if (paymentRepository.existsByPaymentKey(request.paymentKey())) {
            throw new BusinessException(ErrorCode.PAYMENT_KEY_DUPLICATED);
        }
        //금액 검증(0보다 커야 함) - DTO에서 1차 검증, 여기서 방어적 2차 체크
        BigDecimal amount = request.amount();
        if (amount == null || amount.signum() <= 0) {
            throw new BusinessException(ErrorCode.PAYMENT_INVALID_AMOUNT);
        }

        //결제 수단 코드 정규화
        String methodCode = request.methodEnum().code();

        //Payment 생성 및 저장
        Payment payment = Payment.create(request.paymentKey(), amount, methodCode, order);
        paymentRepository.save(payment);

        return payment.getId();
    }

    @Override
    public void approve(Long memberId, Long paymentId, PaymentApproveRequest request) {
        //본인 결제건 조회
        Payment payment = paymentRepository.findByIdAndOrderMemberId(paymentId, memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

        //version 검증
        if (!payment.getVersion().equals(request.version())) {
            throw new BusinessException(ErrorCode.OPTIMISTIC_LOCK_CONFLICT);
        }

        Order order = payment.getOrder();

        //주문 상태 검증 : 아직 결제 확정 전 상태여야 함
        if (order.getStatus() != ORDERED) {
            throw new BusinessException(ErrorCode.PAYMENT_NOT_ALLOWED_FOR_ORDER_STATUS);
        }

        //결제 금액 검증
        BigDecimal approvedAmount = request.approvedAmount();
        if (approvedAmount == null || approvedAmount.signum() <= 0) {
            throw new BusinessException(ErrorCode.PAYMENT_INVALID_AMOUNT);
        }
        if (payment.getAmount().compareTo(approvedAmount) != 0) {
            throw new BusinessException(ErrorCode.PAYMENT_AMOUNT_MISMATCH);
        }

        //결제 상태 전이 가능 여부 확인
        if (payment.getStatus() == PaymentStatus.COMPLETED
                || payment.getStatus() == PaymentStatus.CANCELED
                || payment.getStatus() == PaymentStatus.FAILED) {
            throw new BusinessException(ErrorCode.PAYMENT_INVALID_STATUS_TRANSITION);
        }

        //PaymentStatus.READY였다면 IN_PROGRESS로 한 번 올리고, 최종 COMPLETED 처리
        if (payment.getStatus() == PaymentStatus.READY) {
            payment.markInProgress();
        }
        payment.complete();

        //TODO : order 상태 PAID 전이(Order 도메인 구조 확인하고 맞춰보기)
    }

    @Override
    public void cancel(Long memberId, Long paymentId, PaymentCancelRequest request) {
        //본인 결제건 조회
        Payment payment = paymentRepository.findByIdAndOrderMemberId(paymentId, memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

        //version 검증
        if (!payment.getVersion().equals(request.version())) {
            throw new BusinessException(ErrorCode.OPTIMISTIC_LOCK_CONFLICT);
        }

        //현재 상태에서 취소 가능한지 검증
        if (payment.getStatus() == PaymentStatus.COMPLETED
                || payment.getStatus() == PaymentStatus.CANCELED
                || payment.getStatus() == PaymentStatus.FAILED) {
            throw new BusinessException(ErrorCode.PAYMENT_INVALID_STATUS_TRANSITION);
        }

        payment.cancel();

        /**TODO : 취소 사유는 현재 Payment 엔티티에 필드를 두지 않았으므로,
            향후 PaymentCancelHistory 또는 로그 테이블로 남기는게 좋을 것 같음 */
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getByOrderId(Long memberId, Long orderId) {
        //본인 주문인지 검증
        Order order = orderRepository.findByIdAndMemberId(orderId, memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        //주문 기준 결제 조회
        Payment payment = paymentRepository.findByOrderId(order.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

        return toResponse(payment);
    }

    private PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getOrder().getId(),
                payment.getPaymentKey(),
                payment.getAmount(),
                payment.getMethod(),
                payment.getStatus(),
                payment.getVersion(),
                payment.getCreatedDate(),
                payment.getLastModifiedDate()
        );
    }
}