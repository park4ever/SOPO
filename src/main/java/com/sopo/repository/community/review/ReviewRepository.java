package com.sopo.repository.community.review;

import com.sopo.domain.community.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
