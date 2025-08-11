package com.sopo.domain.qna;

import com.sopo.common.BaseEntity;
import com.sopo.domain.item.Item;
import com.sopo.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.sopo.domain.qna.QnaStatus.*;
import static jakarta.persistence.EnumType.*;
import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

@Entity
@Getter
@Table(name = "qna_question",
    indexes = {
        @Index(name = "idx_qna_question_item_private", columnList = "item_id, is_private"),
        @Index(name = "idx_qna_question_member_created", columnList = "member_id, created_at")
    }
)
@NoArgsConstructor(access = PROTECTED)
public class Question extends BaseEntity {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "question_id")
    private Long id;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "member_id")
    private Member asker;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(length = 100)
    private String title;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false, name = "is_private")
    private boolean isPrivate;

    @Enumerated(STRING)
    @Column(nullable = false, length = 20)
    private QnaStatus status;

    @Column(name = "answered_at")
    private LocalDateTime answeredAt;

    private Question(Member asker, Item item, String title, String content, boolean isPrivate) {
        this.asker = asker;
        this.item = item;
        this.title = title;
        this.content = content;
        this.isPrivate = isPrivate;     //TODO 기본값은 컨트롤러나 서비스에서 false로 설정.
        this.status = OPEN;             //생성 시 항상 OPEN
    }

    public static Question create(Member asker, Item item, String title, String content, boolean isPrivate) {
        return new Question(asker, item, title, content, isPrivate);
    }

    public boolean isOwner(Member loginMember) {
        return loginMember != null
            && this.asker != null
            && this.asker.getId() != null
            && this.asker.getId().equals(loginMember.getId());
    }

    public void markAnswered() {
        if (this.status == ANSWERED) return;
        this.status = ANSWERED;
        this.answeredAt = LocalDateTime.now();
    }

    public void close() {
        if (this.status == CLOSED) return;
        this.status = CLOSED;
    }

    //TODO 컨트롤러나 서비스에서 "답변 전(OPEN) + 작성자" 확인 후 호출
    public void updateContent(String newTitle, String newContent) {
        this.title = newTitle;
        this.content = newContent;
    }

    //상태가 OPEN인지 확인(서비스에서 수정 가능 여부 판단에 필요)
    public boolean isOpen() {
        return this.status == OPEN;
    }
}
