package com.sopo.domain.review;

import com.sopo.common.BaseEntity;
import com.sopo.domain.item.Item;
import com.sopo.domain.member.Member;
import com.sopo.domain.order.OrderItem;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

@Entity
@Getter
@Table(name = "review")
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

    public boolean isOwner(Member loginMember) {
        return loginMember != null
            && this.member != null
            && this.member.getId() != null
            && this.member.getId().equals(loginMember.getId());
    }
}