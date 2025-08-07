package com.sopo.domain.cart;

import com.sopo.common.BaseEntity;
import com.sopo.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
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
@Table(name = "cart")
@NoArgsConstructor(access = PROTECTED)
public class Cart extends BaseEntity {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "cart_id")
    private Long id;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "cart", cascade = ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    private Cart(Member member) {
        this.member = member;
    }

    public static Cart create(Member member) {
        return new Cart(member);
    }

    public void addCartItem(CartItem cartItem) {
        cartItems.add(cartItem);
        if (cartItem.getCart() != this) {
            cartItem.assignCart(this);
        }
    }
}
