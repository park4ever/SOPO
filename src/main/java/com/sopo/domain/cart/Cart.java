package com.sopo.domain.cart;

import com.sopo.common.BaseEntity;
import com.sopo.domain.member.Member;
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
@Table(name = "cart")
@NoArgsConstructor(access = PROTECTED)
public class Cart extends BaseEntity {

    /* TODO [서비스 고려사항]
     - Cart 생성 시점: 회원가입 시 자동 생성? vs 첫 담기 시 생성?
     → 현재는 create(Member)로 수동 생성. 서비스에서 결정 필요 */


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
