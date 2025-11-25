package com.sopo.domain.community.review;

import com.sopo.domain.common.BaseEntity;
import com.sopo.domain.item.Item;
import com.sopo.domain.member.Member;
import com.sopo.domain.order.OrderItem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

@Entity
@Getter
@Table(
        name = "review",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_review_order_item", columnNames = {"order_item_id"})
        },
        indexes = {
                @Index(name = "idx_review_item_created", columnList = "item_id, created_at"),
                @Index(name = "idx_review_member_created", columnList = "member_id, created_at")
        }
)
@NoArgsConstructor(access = PROTECTED)
public class Review extends BaseEntity {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "item_id")
    private Item item;

    @OneToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "order_item_id", unique = true)
    private OrderItem orderItem;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false)
    private int rating;

    @Version
    private Long version;

    @OneToMany(mappedBy = "review", cascade = ALL, orphanRemoval = true)
    private List<ReviewImage> images = new ArrayList<>();

    private Review(Member member, Item item, OrderItem orderItem, String content, int rating) {
        this.member = member;
        this.item = item;
        this.orderItem = orderItem;
        this.content = content;
        this.rating = rating;
    }

    public static Review create(Member member, Item item, OrderItem orderItem, String content, int rating) {
        return new Review(member, item, orderItem, content, rating);
    }

    public boolean isOwner(Long memberId) {
        return memberId != null && member != null && memberId.equals(member.getId());
    }

    public void changeContent(String content) {
        this.content = content;
    }

    public void changeRating(int rating) {
        this.rating = rating;
    }

    public void addImage(ReviewImage image) {
        images.add(image);
        if (image.getReview() != this) image.assignReview(this);
    }

    public void removeImage(Long imageId) {
        images.removeIf(img -> Objects.equals(img.getId(), imageId));
    }
}