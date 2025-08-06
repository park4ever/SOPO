package com.sopo.domain.item;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Table(name = "item_color")
@NoArgsConstructor(access = PROTECTED)
public class ItemColor {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "item_color_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String name;

    private ItemColor(String name) {
        this.name = name;
    }

    public static ItemColor create(String name) {
        return new ItemColor(name);
    }
}
