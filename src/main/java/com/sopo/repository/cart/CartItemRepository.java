package com.sopo.repository.cart;

import com.sopo.domain.cart.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartIdAndItemOptionId(Long cartId, Long itemOptionId);
    List<CartItem> findAllByCartId(Long cartId);

    @Query("""
    select distinct ci from CartItem ci
    join fetch ci.itemOption io
    join fetch io.item i
    left join fetch i.images img
        on img.item = i and img.isThumbnail = true
    where ci.cart.id = :cartId
    """)
    List<CartItem> findWithItemGraphByCartIdOnlyThumbnail(@Param("cartId") Long cartId);
}