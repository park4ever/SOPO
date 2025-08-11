package com.sopo.domain.item;

import com.sopo.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

@Entity
@Getter
@Table(name = "item_image")
@NoArgsConstructor(access = PROTECTED)
public class ItemImage extends BaseEntity {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "item_image_id")
    private Long id;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(nullable = false, length = 255)
    private String imageUrl;

    @Column(nullable = false, name = "is_thumbnail")
    private boolean isThumbnail;

    @Column(nullable = false)
    private int sortOrder;

    private ItemImage(String imageUrl, boolean isThumbnail, int sortOrder) {
        this.imageUrl = imageUrl;
        this.isThumbnail = isThumbnail;
        this.sortOrder = sortOrder;
    }

    public static ItemImage create(String imageUrl, boolean isThumbnail, int sortOrder) {
        return new ItemImage(imageUrl, isThumbnail, sortOrder);
    }

    public void assignItem(Item item) {
        this.item = item;
    }

    public void changeSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public void markAsThumbnail() {
        this.isThumbnail = true;
    }

    public void unsetThumbnail() {
        this.isThumbnail = false;
    }
}
