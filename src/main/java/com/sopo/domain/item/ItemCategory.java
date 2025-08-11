package com.sopo.domain.item;

import com.sopo.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
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

    public void addChild(ItemCategory child) {
        this.children.add(child);
        if (child.parent != this) {
            child.parent = this;
        }
    }

    public void markAsDeleted() {
        this.isDeleted = true;
    }

    public void unsetDeleted() {
        this.isDeleted = false;
    }
}