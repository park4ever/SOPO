package com.sopo.domain.item;

import com.sopo.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

@Entity
@Getter
@Table(name = "item_category")
@NoArgsConstructor(access = PROTECTED)
public class ItemCategory extends BaseEntity {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "item_category_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_id")
    private ItemCategory parent;

    @OneToMany(mappedBy = "parent", cascade = ALL, orphanRemoval = true)
    private List<ItemCategory> children = new ArrayList<>();

    @Column(nullable = false)
    private int depth;

    @Column(nullable = false, name = "is_deleted")
    private boolean isDeleted;

    private ItemCategory(String name, ItemCategory parent, int depth) {
        this.name = name;
        this.parent = parent;
        this.depth = depth;
        this.isDeleted = false;
    }

    public static ItemCategory create(String name, ItemCategory parent) {
        int depth = (parent == null) ? 0 : parent.depth + 1;
        ItemCategory category = new ItemCategory(name, parent, depth);
        if (parent != null) {
            parent.addChild(category);
        }
        return category;
    }

    public void rename(String newName) { this.name = newName; }

    public boolean isRoot() { return parent == null; }

    public boolean isAncestorOf(ItemCategory other) {
        ItemCategory p = other.parent;
        while (p != null) {
            if (p == this) return true;
            p = p.parent;
        }
        return false;
    }

    /**
     * parent 변경 + depth 재계산, 순환 방지
     */
    public void moveTo(ItemCategory newParent) {
        if (newParent == this || (newParent != null && newParent.isAncestorOf(this))) {
            throw new IllegalArgumentException("순환 참조는 허용되지 않습니다.");
        }

        if (this.parent != null) {
            this.parent.children.remove(this);
        }

        this.parent = newParent;
        if (newParent != null) {
            newParent.children.add(this);
            this.depth = newParent.depth + 1;
        } else {
            this.depth = 0;
        }

        // 자식들까지 depth 재계산
        for (ItemCategory child : this.children) {
            child.updateDepthRecursively(this.depth + 1);
        }
    }

    public List<ItemCategory> getPathFromRoot() {
        LinkedList<ItemCategory> path = new LinkedList<>();
        ItemCategory current = this;
        while (current != null) {
            path.addFirst(current); // root → this
            current = current.parent;
        }
        return path;
    }

    public void addChild(ItemCategory child) {
        this.children.add(child);
        if (child.parent != this) {
            child.parent = this;
        }
    }

    /** 자신만 삭제 (하위는 그대로 유지) */
    public void markDeletedSelf() {
        this.isDeleted = true;
    }

    /** 자신 및 하위 전체 삭제 */
    public void markDeletedCascade() {
        this.isDeleted = true;
        for (ItemCategory child : this.children) {
            if (!child.isDeleted) {
                child.markDeletedCascade();
            }
        }
    }

    /** 자신만 복구 (부모가 삭제 상태면 복구 금지) */
    public void restoreSelf() {
        if (this.parent != null && this.parent.isDeleted) {
            throw new IllegalStateException("삭제된 부모 밑에서는 복원이 불가능합니다.");
        }
        this.isDeleted = false;
    }

    /** 자신 및 하위 전체 복구 (부모가 삭제 상태면 복구 금지) */
    public void restoreCascade() {
        if (this.parent != null && this.parent.isDeleted) {
            throw new IllegalStateException("삭제된 부모 밑에서는 복원이 불가능합니다.");
        }
        this.isDeleted = false;
        for (ItemCategory child : this.children) {
            if (child.isDeleted) {
                child.restoreCascade();
            }
        }
    }

    private void updateDepthRecursively(int newDepth) {
        this.depth = newDepth;
        for (ItemCategory child : this.children) {
            child.updateDepthRecursively(newDepth + 1);
        }
    }
}