package com.sopo.repository.community.review;

import com.sopo.domain.community.review.Review;
import com.sopo.repository.community.review.cond.ReviewSearchCond;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewRepositoryCustom {

    Page<Review> search(ReviewSearchCond cond, Pageable pageable);

    long countByItemId(Long itemId);

    Double findAverageRatingByItemId(Long itemId);
}