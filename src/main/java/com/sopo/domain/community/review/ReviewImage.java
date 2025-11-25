package com.sopo.domain.community.review;

import com.sopo.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

@Entity
@Getter
@Table(
        name = "review_image",
        indexes = {
                @Index(name = "idx_reviewimg_review_sort", columnList = "review_id, sort_order")
        }
)
@NoArgsConstructor(access = PROTECTED)
public class ReviewImage extends BaseEntity {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "review_image_id")
    private Long id;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "review_id")
    private Review review;

    @Column(nullable = false, length = 255)
    private String imageUrl;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder = 0;

    @Column(name = "is_thumbnail", nullable = false)
    private boolean thumbnail = false;

    private ReviewImage(String imageUrl, int sortOrder, boolean thumbnail) {
        this.imageUrl = imageUrl;
        this.sortOrder = sortOrder;
        this.thumbnail = thumbnail;
    }

    public static ReviewImage create(String imageUrl) {
        return new ReviewImage(imageUrl, 0, false);
    }

    public static ReviewImage create(String imageUrl, int sortOrder, boolean thumbnail) {
        return new ReviewImage(imageUrl, sortOrder, thumbnail);
    }

    public void assignReview(Review review) {
        this.review = review;
    }

    public void changeUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void changeSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public void markAsThumbnail() {
        this.thumbnail = true;
    }

    public void unsetThumbnail() {
        this.thumbnail = false;
    }
}