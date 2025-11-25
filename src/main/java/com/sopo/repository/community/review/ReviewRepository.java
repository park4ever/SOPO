package com.sopo.repository.community.review;

import com.sopo.domain.community.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryCustom {

    boolean existsByOrderItemId(Long orderItemId);

    Optional<Review> findByIdAndMemberId(Long reviewId, Long memberId);
}