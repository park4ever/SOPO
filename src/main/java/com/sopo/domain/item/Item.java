package com.sopo.domain.item;

import com.sopo.domain.common.BaseEntity;
import com.sopo.domain.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.*;

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

    @Column(nullable = false, length = 30)
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

    //값 변경
    public void changeName(String name) { this.name = name; }
    public void changeDescription(String description) { this.description = description; }
    public void changePrice(BigDecimal price) { this.price = price; }
    public void changeBrand(String brand) { this.brand = brand; }
    public void changeCategory(ItemCategory category) { this.category = category; }

    //컬렉션 편의
    public Optional<ItemImage> findImage(Long imageId) {
        return images.stream().filter(i -> Objects.equals(i.getId(), imageId)).findFirst();
    }
    public Optional<ItemOption> findOption(Long optionId) {
        return options.stream().filter(o -> Objects.equals(o.getId(), optionId)).findFirst();
    }
    public void removeImage(Long imageId) {
        findImage(imageId).ifPresent(images::remove);
    }
    public void removeOption(Long optionId) {
        findOption(optionId).ifPresent(options::remove);
    }

    //이미지 정렬/썸네일 보조
    public int nextImageSortOrder() {
        return images.stream().mapToInt(ItemImage::getSortOrder).max().orElse(0) + 1;
    }
    public void assignThumbnail(Long imageId) {
        ItemImage target = findImage(imageId).orElseThrow(() -> new IllegalArgumentException("이미지를 찾을 수 없습니다."));
        images.forEach(ItemImage::unsetThumbnail);
        target.markAsThumbnail();
    }
    public void reorderImages(Map<Long, Integer> orders) {
        for (ItemImage img : images) {
            Integer so = orders.get(img.getId());
            if (so != null) img.changeSortOrder(so);
        }
    }

    //옵션 중복 확인(조합 중복 방지 보조)
    public boolean hasOption(ItemColor color, ItemSize size) {
        return options.stream().anyMatch(o ->
                Objects.equals(o.getColor().getId(), color.getId()) &&
                Objects.equals(o.getSize().getId(), size.getId()));
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
    public void decreaseSalesVolume(int amount) {
        this.salesVolume = Math.max(0, this.salesVolume - amount);
    }
    public void changeStatus(ItemStatus status) {
        this.status = status;
    }
}