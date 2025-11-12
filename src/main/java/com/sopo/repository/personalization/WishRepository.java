package com.sopo.repository.personalization;

import com.sopo.domain.personalization.wish.Wish;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WishRepository extends JpaRepository<Wish, Long> {

    Optional<Wish> findByMemberIdAndItemId(Long memberId, Long itemId);

    boolean existsByMemberIdAndItemId(Long memberId, Long itemId);

    @EntityGraph(attributePaths = {"item", "item.images"})
    Page<Wish> findByMemberId(Long memberId, Pageable pageable);

    long countByItemId(Long itemId);

    void deleteByMemberIdAndItemId(Long memberId, Long itemId);
}