package com.sopo.domain.payment;

import com.sopo.common.BaseEntity;
import com.sopo.domain.order.Order;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static com.sopo.domain.payment.PaymentStatus.*;
import static jakarta.persistence.EnumType.*;
import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

@Entity
@Getter
@Table(name = "payment")
@NoArgsConstructor(access = PROTECTED)
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String paymentKey;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 20)
    private String method;

    @Enumerated(STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    private Payment(String paymentKey, BigDecimal amount, String method, Order order) {
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.method = method;
        this.status = READY;
        this.order = order;
    }

    public static Payment create(String paymentKey, BigDecimal amount, String method, Order order) {
        return new Payment(paymentKey, amount, method, order);
    }

    public void markInProgress() {
        this.status = IN_PROGRESS;
    }

    public void complete() {
        this.status = COMPLETED;
    }

    public void fail() {
        this.status = FAILED;
    }

    public void cancel() {
        this.status = CANCELLED;
    }
}