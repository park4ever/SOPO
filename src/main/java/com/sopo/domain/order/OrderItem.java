package com.sopo.domain.order;

import com.sopo.domain.common.BaseEntity;
import com.sopo.domain.item.ItemOption;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

@Entity
@Getter
@Table(name = "order_item")
@NoArgsConstructor(access = PROTECTED)
public class OrderItem extends BaseEntity {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "item_option_id")
    private ItemOption itemOption;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price; //주문 시점 가격 스냅샷

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    private OrderItem(ItemOption itemOption, int quantity) {
        this.itemOption = itemOption;
        this.quantity = quantity;
        this.price = itemOption.getItem().getPrice(); //주문 시점 가격 가져오기
        this.totalPrice = calculateTotalPrice();
    }

    public static OrderItem create(ItemOption itemOption, int quantity) {
        return new OrderItem(itemOption, quantity);
    }

    private BigDecimal calculateTotalPrice() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    public void assignOrder(Order order) {
        this.order = order;
    }

    public void increaseQuantity(int amount) {
        this.quantity += amount;
        this.totalPrice = calculateTotalPrice();
    }

    public void decreaseQuantity(int amount) {
        if (this.quantity - amount < 1) {
            throw new IllegalArgumentException("최소 수량은 1개 이상이어야 합니다.");
        }
        this.quantity -= amount;
        this.totalPrice = calculateTotalPrice();
    }

    public void updateQuantity(int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("최소 수량은 1개 이상이어야 합니다.");
        }
        this.quantity = quantity;
        this.totalPrice = calculateTotalPrice();
    }
}