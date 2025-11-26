package com.sopo.domain.community.qna;

import com.sopo.domain.common.BaseEntity;
import com.sopo.domain.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

@Entity
@Getter
@Table(name = "qna_answer",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_qna_answer_question", columnNames = "question_id")
    }
)
@NoArgsConstructor(access = PROTECTED)
public class Answer extends BaseEntity {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "answer_id")
    private Long id;

    @Version
    private Long version;

    @OneToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "responder_id")
    private Member responder;  //TODO item.seller와 동일함을 서비스에서 검증

    @Column(nullable = false, length = 1000)
    private String content;

    private Answer(Question question, Member responder, String content) {
        this.question = question;
        this.responder = responder;
        this.content = content;

        question.assignAnswer(this);
        question.markAnswered();
    }

    public static Answer create(Question question, Member responder, String content) {
        return new Answer(question, responder, content);
    }

    public void changeContent(String content) {
        this.content = content;
    }

    public Long getQuestionId() {
        return (question != null) ? question.getId() : null;
    }

    public Long getResponderId() {
        return (responder != null) ? responder.getId() : null;
    }
}