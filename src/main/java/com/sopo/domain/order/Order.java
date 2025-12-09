package com.sopo.domain.order;

import com.sopo.common.money.Money;
import com.sopo.domain.common.BaseEntity;
import com.sopo.domain.member.Member;
import com.sopo.exception.BusinessException;
import com.sopo.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.sopo.domain.order.OrderStatus.*;
import static com.sopo.exception.ErrorCode.*;
import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.EnumType.*;
import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

@Entity
@Getter
@Table(name = "orders")
@NoArgsConstructor(access = PROTECTED)
public class Order extends BaseEntity {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "order", cascade = ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Enumerated(STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(nullable = false, name = "is_deleted")
    private boolean isDeleted;

    @Version
    private Long version;

    private Order(Member member, List<OrderItem> orderItems) {
        this.member = member;
        this.status = ORDERED;
        this.totalPrice = calculateTotalPrice(orderItems);
        this.isDeleted = false;
        orderItems.forEach(this::addOrderItem);
    }

    public static Order create(Member member, List<OrderItem> orderItems) {
        return new Order(member, orderItems);
    }

    private BigDecimal calculateTotalPrice(List<OrderItem> orderItems) {
        Money sum = orderItems.stream()
                .map(oi -> Money.of(oi.getTotalPrice()))
                .reduce(Money.ZERO, Money::plus);
        return sum.asBigDecimal();
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        if (orderItem.getOrder() != this) {
            orderItem.assignOrder(this);
        }
    }

    public void cancelByUser() {
        var current = this.status;

        if (current == CANCELED) {
            throw new BusinessException(ORDER_ALREADY_CANCELED);
        }
        if (!current.isUserCancelable()) {
            throw new BusinessException(INVALID_ORDER_STATUS_CHANGE);
        }

        this.status = CANCELED;
    }

    public void changeStatusByAdmin(OrderStatus targetStatus) {
        var current = this.status;

        if (current == targetStatus) {
            return;
        }
        if (!current.canTransitionTo(targetStatus)) {
            throw new BusinessException(INVALID_ORDER_STATUS_CHANGE);
        }

        this.status = targetStatus;
    }

    public void markAsPaid() {
        if (this.status != ORDERED) {
            throw new BusinessException(PAYMENT_NOT_ALLOWED_FOR_ORDER_STATUS);
        }
        this.status = PAID;
    }

    public void markAsDeleted() {
        this.isDeleted = true;
    }

    public void unsetDeleted() {
        this.isDeleted = false;
    }
}