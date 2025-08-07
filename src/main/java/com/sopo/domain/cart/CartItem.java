package com.sopo.domain.cart;

import com.sopo.common.BaseEntity;
import com.sopo.domain.item.ItemOption;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

@Entity
@Getter
@Table(name = "cart_item")
@NoArgsConstructor(access = PROTECTED)
public class CartItem extends BaseEntity {

    /* TODO [서비스 고려사항]
     - 동일한 ItemOption을 중복 추가 시 기존 항목 수량 증가 vs 새 항목 추가?
     - ItemOption이 품절/삭제되었을 때 장바구니 항목은 유지 or 자동 제거?
     → 정책 결정 필요 (UX 관점 포함) */

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "cart_item_id")
    private Long id;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "item_option_id")
    private ItemOption itemOption;

    @Column(nullable = false)
    private int quantity;

    private CartItem(ItemOption itemOption, int quantity) {
        this.itemOption = itemOption;
        this.quantity = quantity;
    }

    public static CartItem create(ItemOption itemOption, int quantity) {
        return new CartItem(itemOption, quantity);
    }

    public void assignCart(Cart cart) {
        this.cart = cart;
    }

    public void changeQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void increaseQuantity(int amount) {
        this.quantity += amount;
    }

    public void decreaseQuantity(int amount) {
        this.quantity = Math.max(0, this.quantity - amount);
    }
}
