package com.sopo.repository.cart;

import com.sopo.domain.cart.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartIdAndItemOptionId(Long cartId, Long itemOptionId);

    @Query("""
    select distinct ci from CartItem ci
    join fetch ci.itemOption io
    join fetch io.item i
    join fetch io.color c
    join fetch io.size s
    left join fetch i.images img
        on img.item = i and img.isThumbnail = true
    where ci.cart.id = :cartId
    order by ci.id asc
    """)
    List<CartItem> findWithItemGraphByCartIdOnlyThumbnail(@Param("cartId") Long cartId);

    @Query("""
    select ci from CartItem ci
    join ci.cart c
    join fetch ci.itemOption io
    join fetch io.item i
    left join fetch i.images img on img.item = i and img.isThumbnail = true
    where ci.id = :cartItemId and c.member.id = :memberId
    """)
    Optional<CartItem> findOwnedWithItem(@Param("cartItemId") Long cartItemId, @Param("memberId") Long memberId);

    @Modifying
    @Query("delete from CartItem ci where ci.cart.id = :cartId")
    void deleteAllByCartId(@Param("cartId") Long cartId);
}