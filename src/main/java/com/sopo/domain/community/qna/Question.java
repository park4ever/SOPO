package com.sopo.domain.community.qna;

import com.sopo.domain.common.BaseEntity;
import com.sopo.domain.item.Item;
import com.sopo.domain.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    @Version
    private Long version;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "member_id")
    private Member asker;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "item_id")
    private Item item;

    @OneToOne(mappedBy = "question", fetch = LAZY)
    private Answer answer;

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
        this.isPrivate = isPrivate;
        this.status = QnaStatus.OPEN;   //생성 시 항상 OPEN
    }

    public static Question create(Member asker, Item item, String title, String content, boolean isPrivate) {
        return new Question(asker, item, title, content, isPrivate);
    }

    public void assignAnswer(Answer answer) {
        this.answer = answer;
    }

    public boolean isOwner(Long memberId) {
        return memberId != null
                && this.asker != null
                && this.asker.getId() != null
                && this.asker.getId().equals(memberId);
    }

    public Long getAskerId() {
        return (asker != null) ? asker.getId() : null;
    }

    public Long getItemId() {
        return (item != null) ? item.getId() : null;
    }

    public void markAnswered() {
        if (this.status == QnaStatus.ANSWERED) return;
        this.status = QnaStatus.ANSWERED;
        this.answeredAt = LocalDateTime.now();
    }

    public void close() {
        if (this.status == QnaStatus.CLOSED) return;
        this.status = QnaStatus.CLOSED;
    }

    //TODO 컨트롤러나 서비스에서 "답변 전(OPEN) + 작성자" 확인 후 호출
    public void updateContent(String newTitle, String newContent) {
        this.title = newTitle;
        this.content = newContent;
    }

    public boolean isOpen() {
        return this.status == QnaStatus.OPEN;
    }

    public boolean isAnswered() {
        return this.status == QnaStatus.ANSWERED;
    }

    public boolean isClosed() {
        return this.status == QnaStatus.CLOSED;
    }
}