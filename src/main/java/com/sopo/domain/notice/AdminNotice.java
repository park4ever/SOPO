package com.sopo.domain.notice;

import com.sopo.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

@Entity
@Getter
@Table(
        name = "admin_notice",
        indexes = {
                @Index(name = "idx_notice_pinned_created", columnList = "is_pinned, created_at")
        }
)
@NoArgsConstructor(access = PROTECTED)
public class AdminNotice extends BaseEntity {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "notice_id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 4000)
    private String content;

    @Column(nullable = false, name = "is_pinned")
    private boolean pinned;

    private AdminNotice(String title, String content, boolean pinned) {
        this.title = title;
        this.content = content;
        this.pinned = pinned;
    }

    public static AdminNotice create(String title, String content, boolean pinned) {
        return new AdminNotice(title, content, pinned);
    }

    public void updateContent(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void pin() {
        this.pinned = true;
    }

    public void unpin() {
        this.pinned = false;
    }
}