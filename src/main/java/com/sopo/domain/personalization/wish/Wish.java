package com.sopo.domain.personalization.wish;

import com.sopo.domain.common.BaseEntity;
import com.sopo.domain.item.Item;
import com.sopo.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

@Entity
@Getter
@Table(
        name = "wish",
        uniqueConstraints = {
            @UniqueConstraint(name = "uk_wish_member_item", columnNames = {"member_id", "item_id"})
        },
        indexes = {
            @Index(name = "idx_wish_member_created", columnList = "member_id, created_at")
        }
)
@NoArgsConstructor(access = PROTECTED)
public class Wish extends BaseEntity {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "wish_id")
    private Long id;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "item_id")
    private Item item;

    private Wish(Member member, Item item) {
        this.member = member;
        this.item = item;
    }

    public static Wish create(Member member, Item item) {
        return new Wish(member, item);
    }

    public boolean isOwner(Long memberId) {
        return memberId != null && member != null && memberId.equals(member.getId());
    }

    public Long getMemberId() {
        return member != null ? member.getId() : null;
    }

    public Long getItemId() {
        return item != null ? item.getId() : null;
    }
}