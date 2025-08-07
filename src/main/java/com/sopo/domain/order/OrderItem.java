package com.sopo.domain.order;

import com.sopo.common.BaseEntity;
import com.sopo.domain.item.Item;
import com.sopo.domain.item.ItemOption;
import jakarta.persistence.*;
import lombok.AccessLevel;
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
    private BigDecimal totalPrice;

    public OrderItem(ItemOption itemOption, int quantity) {
        this.itemOption = itemOption;
        this.quantity = quantity;
        this.totalPrice = calculateTotalPrice(itemOption, quantity);
    }

    public static OrderItem create(ItemOption itemOption, int quantity) {
        return new OrderItem(itemOption, quantity);
    }

    private BigDecimal calculateTotalPrice(ItemOption itemOption, int quantity) {
        Item item = itemOption.getItem();
        return item.getPrice().multiply(BigDecimal.valueOf(quantity));
    }

    public void assignOrder(Order order) {
        this.order = order;
    }
}
