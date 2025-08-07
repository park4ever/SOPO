package com.sopo.domain.review;

import com.sopo.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

@Entity
@Getter
@Table(name = "review_image")
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

    private ReviewImage(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public static ReviewImage create(String imageUrl) {
        return new ReviewImage(imageUrl);
    }

    public void assignReview(Review review) {
        this.review = review;
    }
}