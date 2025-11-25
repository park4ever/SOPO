package com.sopo.service.review;

import com.sopo.domain.community.review.Review;
import com.sopo.domain.community.review.ReviewImage;
import com.sopo.domain.item.Item;
import com.sopo.domain.member.Member;
import com.sopo.domain.order.Order;
import com.sopo.domain.order.OrderItem;
import com.sopo.domain.order.OrderStatus;
import com.sopo.dto.review.request.ReviewCreateRequest;
import com.sopo.dto.review.request.ReviewUpdateRequest;
import com.sopo.dto.review.response.ReviewImageResponse;
import com.sopo.dto.review.response.ReviewResponse;
import com.sopo.dto.review.response.ReviewSummaryResponse;
import com.sopo.exception.BusinessException;
import com.sopo.exception.ErrorCode;
import com.sopo.repository.community.review.ReviewRepository;
import com.sopo.repository.community.review.cond.ReviewSearchCond;
import com.sopo.repository.order.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    @Transactional
    public Long create(Long memberId, ReviewCreateRequest request) {
        Long itemId = request.itemId();
        Long orderItemId = request.orderItemId();

        //주문 항목 조회
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_ITEM_NOT_FOUND));

        Order order = orderItem.getOrder();
        Item item = orderItem.getItemOption().getItem();
        Member buyer = order.getMember();

        //로그인 회원이 이 주문의 구매자인지 검증
        if (buyer == null || !Objects.equals(buyer.getId(), memberId)) {
            throw new BusinessException(ErrorCode.ORDER_ACCESS_DENIED);
        }
        //요청 itemId와 실제 주문 상품의 itemId가 일치하는지 검증
        if (!Objects.equals(item.getId(), itemId)) {
            throw new BusinessException(ErrorCode.INVALID_PARAM);
        }
        //주문 상태가 리뷰 작성 가능한 상태인지 검증
        if (!isReviewAllowedStatus(order.getStatus())) {
            throw new BusinessException(ErrorCode.REVIEW_NOT_ALLOWED_FOR_ORDER_STATUS);
        }
        //동일 주문 항목에 이미 리뷰가 있는지 검증
        if (reviewRepository.existsByOrderItemId(orderItemId)) {
            throw new BusinessException(ErrorCode.REVIEW_ALREADY_EXISTS);
        }
        //평점 검증
        validateRating(request.rating());

        //엔티티 생성
        Review review = Review.create(buyer, item, orderItem, request.content(), request.rating());

        //이미지 생성(존재할 경우)
        List<String> imageUrls = request.imageUrls();
        if (imageUrls != null && !imageUrls.isEmpty()) {
            int sortOrder = 0;
            for (String url : imageUrls) {
                if (url == null || url.isBlank()) continue;
                ReviewImage image = ReviewImage.create(url, sortOrder++, false);
                review.addImage(image);
            }
        }

        reviewRepository.save(review);
        return review.getId();
    }

    @Override
    @Transactional
    public void update(Long memberId, Long reviewId, ReviewUpdateRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));

        //소유자 검증
        if (!review.isOwner(memberId)) {
            throw new BusinessException(ErrorCode.REVIEW_FORBIDDEN_ACCESS);
        }
        //낙관 락 검증
        if (request.version() == null || !Objects.equals(request.version(), review.getVersion())) {
            throw new BusinessException(ErrorCode.OPTIMISTIC_LOCK_CONFLICT);
        }

        //내용 변경
        if (request.content() != null) {
            review.changeContent(request.content());
        }
        //평점 변경
        if (request.rating() != null) {
            validateRating(request.rating());
            review.changeRating(request.rating());
        }
        //이미지 변경
        if (request.imageUrls() != null) {
            //null이 아니면 이미지 전체 교체
            List<String> imageUrls = request.imageUrls();

            //기존 이미지 전체 제거
            review.getImages().clear();

            if (!imageUrls.isEmpty()) {
                int sortOrder = 0;
                for (String url : imageUrls) {
                    if (url == null || url.isBlank()) continue;
                    ReviewImage image = ReviewImage.create(url, sortOrder++, false);
                    review.addImage(image);
                }
            }
        }
    }

    @Override
    @Transactional
    public void delete(Long memberId, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));

        if (!review.isOwner(memberId)) {
            throw new BusinessException(ErrorCode.REVIEW_FORBIDDEN_ACCESS);
        }
        reviewRepository.delete(review);
    }

    @Override
    public ReviewResponse getById(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));
        return toResponse(review);
    }

    @Override
    public Page<ReviewResponse> getByItem(Long itemId, Pageable pageable) {
        var cond = ReviewSearchCond.forItem(itemId);
        return reviewRepository.search(cond, pageable)
                .map(this::toResponse);
    }

    @Override
    public Page<ReviewResponse> getByMember(Long memberId, Pageable pageable) {
        var cond = ReviewSearchCond.forMember(memberId);
        return reviewRepository.search(cond, pageable)
                .map(this::toResponse);
    }

    @Override
    public ReviewSummaryResponse getSummaryForItem(Long itemId) {
        long count = reviewRepository.countByItemId(itemId);
        Double avg = reviewRepository.findAverageRatingByItemId(itemId);
        return new ReviewSummaryResponse(count, avg);
    }

    @Override
    public boolean existsForOrderItem(Long memberId, Long orderItemId) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_ITEM_NOT_FOUND));

        Order order = orderItem.getOrder();
        Member buyer = order.getMember();

        if (buyer == null || !Objects.equals(buyer.getId(), memberId)) {
            throw new BusinessException(ErrorCode.ORDER_ACCESS_DENIED);
        }

        return reviewRepository.existsByOrderItemId(orderItemId);
    }

    private boolean isReviewAllowedStatus(OrderStatus status) {
        if (status == null) return false;
        return switch (status) {
            case DELIVERED, RETURNED, REFUNDED -> true;
            default -> false;
        };
    }

    private void validateRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new BusinessException(ErrorCode.REVIEW_INVALID_RATING);
        }
    }

    private ReviewResponse toResponse(Review review) {
        Item item = review.getItem();
        Member member = review.getMember();

        List<ReviewImageResponse> images = review.getImages().stream()
                .sorted(Comparator.comparingInt(ReviewImage::getSortOrder))
                .map(img -> new ReviewImageResponse(
                        img.getId(),
                        img.getImageUrl(),
                        img.getSortOrder(),
                        img.isThumbnail()
                ))
                .toList();

        return new ReviewResponse(
                review.getId(),
                item.getId(),
                item.getName(),
                item.getBrand(),
                item.getPrice(),
                item.getStatus(),
                member.getId(),
                member.getName(),
                review.getContent(),
                review.getRating(),
                review.getCreatedDate(),
                review.getLastModifiedDate(),
                review.getVersion(),
                images
        );
    }
}