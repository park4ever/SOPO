package com.sopo.domain.item;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

@Entity
@Getter
@Table(name = "item_size")
@NoArgsConstructor(access = PROTECTED)
public class ItemSize {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "item_size_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String name;

    private ItemSize(String name) {
        this.name = name;
    }

    public static ItemSize create(String name) {
        return new ItemSize(name);
    }
}
