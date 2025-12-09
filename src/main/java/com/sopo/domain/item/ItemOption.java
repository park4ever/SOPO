package com.sopo.domain.item;

import com.sopo.domain.common.BaseEntity;
import com.sopo.exception.BusinessException;
import com.sopo.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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

    @Version
    private Long version;

    private ItemOption(ItemColor color, ItemSize size, int stock) {
        this.color = color;
        this.size = size;
        this.stock = stock;
        this.isSoldOut = (stock <= 0);
    }

    public static ItemOption create(ItemColor color, ItemSize size, int stock) {
        return new ItemOption(color, size, stock);
    }

    public BigDecimal snapshotUnitPrice() {
        if (item == null || item.getPrice() == null) {
            throw new BusinessException(ErrorCode.ITEM_NOT_FOUND);
        }
        return item.getPrice();
    }

    public boolean isCombinationOf(ItemColor color, ItemSize size) {
        return Objects.equals(this.color.getId(), color.getId())
            && Objects.equals(this.size.getId(), size.getId());
    }

    public void assignItem(Item item) {
        this.item = item;
    }

    // NOTE: 재고 동시성 제어는 서비스/프로세서(OrderPlacementProcessor 등) 레벨에서
    // PESSIMISTIC_WRITE 또는 @Version을 통해 보장해야 한다.
    // 이 메서드는 "이미 락이 잡힌 상태"에서만 호출된다는 전제를 갖는다.
    public void decreaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("차감할 수량은 1 이상이어야 합니다.");
        }
        if (this.stock < quantity) {
            throw new BusinessException(ErrorCode.QUANTITY_EXCEEDS_STOCK);
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