package com.sopo.domain.community.post;

import com.sopo.domain.common.BaseEntity;
import com.sopo.domain.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

@Entity
@Getter
@Table(
        name = "post",
        indexes = {
            @Index(name = "idx_post_created", columnList = "created_at"),
            @Index(name = "idx_post_member_created", columnList = "member_id, created_at")
        }
)
@NoArgsConstructor(access = PROTECTED)
public class Post extends BaseEntity {

    //TODO viewCount(조회수)는 추후 별도 전략(배치/캐시/증가 쿼리 최적화)을 함께 설계

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @Version
    private Long version;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "member_id")
    private Member author;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 2000)
    private String content;

    private Post(Member author, String title, String content) {
        this.author = author;
        this.title = title;
        this.content = content;
    }

    public static Post create(Member author, String title, String content) {
        return new Post(author, title, content);
    }

    //TODO 서비스에서 권한/조건 체크 후 호출
    public void updateContent(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public Long getAuthorId() {
        return (author != null) ? author.getId() : null;
    }

    public boolean isOwner(Long memberId) {
        return memberId != null && Objects.equals(getAuthorId(), memberId);
    }
}