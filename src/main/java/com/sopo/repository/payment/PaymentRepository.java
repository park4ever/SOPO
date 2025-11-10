    package com.sopo.repository.payment;

import com.sopo.domain.payment.Payment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

    public interface PaymentRepository extends JpaRepository<Payment, Long> {

        Optional<Payment> findByOrderId(Long orderId);

        Optional<Payment> findByPaymentKey(String paymentKey);

        boolean existsByOrderId(Long orderId);

        boolean existsByPaymentKey(String paymentKey);

        Optional<Payment> findByIdAndOrderMemberId(Long id, Long memberId);

        @EntityGraph(attributePaths = {"order"})
        Optional<Payment> findWithOrderByOrderId(Long orderId);
}