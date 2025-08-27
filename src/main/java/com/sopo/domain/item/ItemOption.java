package com.sopo.domain.item;

import com.sopo.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

@Entity
@Getter
@Table(name = "item_option")
@NoArgsConstructor(access = PROTECTED)
public class ItemOption extends BaseEntity {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "item_option_id")
    private Long id;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "color_id")
    private ItemColor color;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "size_id")
    private ItemSize size;

    @Column(nullable = false)
    private int stock;

    @Column(nullable = false, name = "is_sold_out")
    private boolean isSoldOut;

    private ItemOption(ItemColor color, ItemSize size, int stock) {
        this.color = color;
        this.size = size;
        this.stock = stock;
        this.isSoldOut = (stock <= 0);
    }

    public static ItemOption create(ItemColor color, ItemSize size, int stock) {
        return new ItemOption(color, size, stock);
    }

    public boolean isCombinationOf(ItemColor color, ItemSize size) {
        return Objects.equals(this.color.getId(), color.getId())
            && Objects.equals(this.size.getId(), size.getId());
    }

    public void assignItem(Item item) {
        this.item = item;
    }

    //TODO 동시 주문 시 재고 차감에 대한 동시성 보장 필요 (서비스 단에서 Lock 처리 예정)
    public void decreaseStock(int quantity) {
        if (this.stock < quantity) {
            throw new IllegalArgumentException("상품의 재고가 부족합니다.");
        }
        this.stock -= quantity;
        if (this.stock == 0) {
            this.isSoldOut = true;
        }
    }

    public void increaseStock(int quantity) {
        this.stock += quantity;
        if (this.stock > 0) {
            this.isSoldOut = false;
        }
    }

    public void markSoldOut() {
        this.isSoldOut = true;
    }

    public void markInStock() {
        if (this.stock > 0) {
            this.isSoldOut = false;
        }
    }
}