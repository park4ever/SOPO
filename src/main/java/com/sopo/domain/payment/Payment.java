package com.sopo.domain.payment;

import com.sopo.domain.common.BaseEntity;
import com.sopo.domain.order.Order;
import com.sopo.exception.BusinessException;
import com.sopo.exception.ErrorCode;
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

    @Version
    private Long version;

    private Payment(String paymentKey, BigDecimal amount, String method, Order order) {
        if (paymentKey == null || paymentKey.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_PARAM);
        }
        if (amount == null || amount.signum() <= 0) {
            throw new BusinessException(ErrorCode.PAYMENT_INVALID_AMOUNT);
        }
        if (method == null || method.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_PARAM);
        }

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
        if (this.status != READY) {
            throw new BusinessException(ErrorCode.PAYMENT_INVALID_STATUS_TRANSITION);
        }
        this.status = IN_PROGRESS;
    }

    public void complete() {
        if (this.status != IN_PROGRESS) {
            throw new BusinessException(ErrorCode.PAYMENT_INVALID_STATUS_TRANSITION);
        }
        this.status = COMPLETED;
    }

    public void fail() {
        if (this.status != READY && this.status != IN_PROGRESS) {
            throw new BusinessException(ErrorCode.PAYMENT_INVALID_STATUS_TRANSITION);
        }
        this.status = FAILED;
    }

    public void cancel() {
        if (this.status != READY && this.status != IN_PROGRESS) {
            throw new BusinessException(ErrorCode.PAYMENT_INVALID_STATUS_TRANSITION);
        }
        this.status = CANCELED;
    }

    public Long getOrderId() {
        return (order != null) ? order.getId() : null;
    }
}