package com.sopo.domain;

import com.sopo.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.EnumType.*;
import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

@Entity
@Getter
@Table(name = "item")
@NoArgsConstructor(access = PROTECTED)
public class Item extends BaseEntity {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "item_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(length = 30)
    private String brand;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "seller_id")
    private Member seller;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "category_id")
    private ItemCategory category;

    @OneToMany(mappedBy = "item", cascade = ALL, orphanRemoval = true)
    private List<ItemImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "item", cascade = ALL, orphanRemoval = true)
    private List<ItemOption> options = new ArrayList<>();

    @Enumerated(STRING)
    @Column(nullable = false)
    private ItemStatus status;

    @Column(nullable = false)
    private int salesVolume;

    @Column(nullable = false, name = "is_deleted")
    private boolean isDeleted;

    private Item(String name, String description, BigDecimal price, String brand,
                 Member seller, ItemCategory category, ItemStatus status) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.brand = brand;
        this.seller = seller;
        this.category = category;
        this.status = status;
        this.salesVolume = 0;
        this.isDeleted = false;
    }

    public static Item create(String name, String description, BigDecimal price, String brand, Member seller, ItemCategory category) {
        return new Item(name, description, price, brand, seller, category, ItemStatus.ON_SALE);
    }

    public void addImage(ItemImage image) {
        images.add(image);
        if (image.getItem() != this) {
            image.assignItem(this);
        }
    }

    public void addOption(ItemOption option) {
        options.add(option);
        if (option.getItem() != this) {
            option.assignItem(this);
        }
    }

    public void markAsDeleted() {
        this.isDeleted = true;
    }

    public void unsetDeleted() {
        this.isDeleted = false;
    }

    public void increaseSalesVolume(int amount) {
        this.salesVolume += amount;
    }

    public void changeStatus(ItemStatus status) {
        this.status = status;
    }
}