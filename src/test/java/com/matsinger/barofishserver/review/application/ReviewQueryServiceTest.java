package com.matsinger.barofishserver.review.application;

import com.matsinger.barofishserver.order.orderprductinfo.application.OrderProductInfoQueryService;
import com.matsinger.barofishserver.product.application.ProductQueryService;
import com.matsinger.barofishserver.review.domain.*;
import com.matsinger.barofishserver.review.dto.v2.ReviewDtoV2;
import com.matsinger.barofishserver.review.dto.v2.ReviewEvaluationSummaryDto;
import com.matsinger.barofishserver.review.repository.ReviewEvaluationRepository;
import com.matsinger.barofishserver.review.repository.ReviewLikeRepository;
import com.matsinger.barofishserver.review.repository.ReviewRepository;
import com.matsinger.barofishserver.review.repository.ReviewRepositoryImpl;
import com.matsinger.barofishserver.store.application.StoreQueryService;
import com.matsinger.barofishserver.user.application.UserQueryService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("local")
@Transactional
class ReviewQueryServiceTest {
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ReviewEvaluationRepository reviewEvaluationRepository;
    @Autowired private ReviewLikeRepository reviewLikeRepository;

    @Autowired
    private ReviewCommandService reviewCommandService;
    @Autowired
    private ProductQueryService productQueryService;
    @Autowired
    private StoreQueryService storeQueryService;
    @Autowired
    private UserQueryService userQueryService;
    @Autowired
    private OrderProductInfoQueryService orderProductInfoQueryService;
    @Autowired
    private ReviewQueryService reviewQueryService;
    @Autowired private ReviewRepositoryImpl reviewRepositoryImpl;


    @Autowired
    private EntityManager em;

    private Review review1;
    private Review review2;

    @BeforeEach
    public void ReviewCommandServiceTest() {
        Review createdReview = reviewRepository.save(Review.builder()
                .productId(10000)
                .storeId(10000)
                .userId(10000)
                .orderProductInfoId(10000)
                .images("[]")
                .content("test")
                .createdAt(Timestamp.valueOf(LocalDateTime.now().minusDays(1)))
                .isDeleted(false)
                .build());
        ReviewEvaluation createdReviewEvaluation = reviewEvaluationRepository.save(ReviewEvaluation.builder()
                .reviewId(createdReview.getId())
                .evaluation(ReviewEvaluationType.TASTE)
                .build());

        Review createdReview2 = reviewRepository.save(Review.builder()
                .productId(10000)
                .storeId(10000)
                .userId(10000)
                .orderProductInfoId(10001)
                .images("[]")
                .content("test")
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .isDeleted(false)
                .build());
        ReviewEvaluation createdReviewEvaluation2 = reviewEvaluationRepository.save(ReviewEvaluation.builder()
                .reviewId(createdReview2.getId())
                .evaluation(ReviewEvaluationType.TASTE)
                .build());

        LocalDateTime now = LocalDateTime.now();

        this.review1 = createdReview;
        this.review2 = createdReview2;
    }

    @DisplayName("리뷰를 리뷰 좋아요 수와 함께 가져올 수 있다.")
    @Test
    void getProductReviewsWithReviewLikeOrderByReviewLike() {
        // given
        reviewLikeRepository.save(ReviewLike.builder()
                .userId(10001)
                .reviewId(review1.getId())
                .build());
        PageRequest pageRequest = PageRequest.of(0, 10);
        // when
        List<ReviewDtoV2> productReviews = reviewRepositoryImpl.getPagedProductReviews(10000, ReviewOrderByType.BEST, pageRequest);

        // then
        assertThat(productReviews.get(0).getLikeSum()).isEqualTo(1);
        assertThat(productReviews.get(1).getLikeSum()).isEqualTo(0);
    }

    @DisplayName("리뷰를 최신순으로 정렬할 수 있다.")
    @Test
    void getProductReviewsWithReviewLikeOrderByCreatedAt() {
        reviewLikeRepository.save(ReviewLike.builder()
                .userId(10001)
                .reviewId(review1.getId())
                .build());
        PageRequest pageRequest = PageRequest.of(0, 10);
        // when
        List<ReviewDtoV2> productReviews = reviewRepositoryImpl.getPagedProductReviews(10000, ReviewOrderByType.RECENT, pageRequest);

        // then
        assertThat(productReviews.get(0).getLikeSum()).isEqualTo(0);
        assertThat(productReviews.get(1).getLikeSum()).isEqualTo(1);
    }

    @DisplayName("리뷰가 삭제 처리된 경우 조회되지 않아야 한다.")
    @Test
    void getProductReviewsExceptDeletedReviews() {
        // given
        Review createdReview3 = reviewRepository.save(Review.builder()
                .productId(10000)
                .storeId(10000)
                .userId(10000)
                .orderProductInfoId(10001)
                .images("[]")
                .content("test")
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .isDeleted(true)
                .build());
        ReviewEvaluation createdReviewEvaluation3 = reviewEvaluationRepository.save(ReviewEvaluation.builder()
                .reviewId(createdReview3.getId())
                .evaluation(ReviewEvaluationType.TASTE)
                .build());
        PageRequest pageRequest = PageRequest.of(0, 10);
        // when
        List<ReviewDtoV2> productReviews = reviewRepositoryImpl.getPagedProductReviews(10000, ReviewOrderByType.BEST, pageRequest);
        // then
        assertThat(productReviews.size()).isEqualTo(2);
    }

    @DisplayName("리뷰의 맛, 포장 등 evaluation의 합계를 집계할 수 있다.")
    @Test
    void testMethodNameHere() {
        // given

        // when
        List<ReviewEvaluationSummaryDto> productReviewEvaluations = reviewRepositoryImpl.getProductReviewEvaluations(10000);

        ReviewEvaluationSummaryDto targetDto = null;
        for (ReviewEvaluationSummaryDto productReviewEvaluation : productReviewEvaluations) {
            System.out.println("productReviewEvaluation = " + productReviewEvaluation);
            if (productReviewEvaluation.getEvaluationType().equals(ReviewEvaluationType.TASTE)) {
                targetDto = productReviewEvaluation;
            }
        }
        // then
        assertThat(targetDto.getEvaluationSum()).isEqualTo(2);
    }

    @DisplayName("스토어 기준으로 판매하는 상품들을 도움돼요 순으로 정렬할 수 있다.")
    @Test
    void storeProductReviewOrderByBestTest() {
        // given
        int productId = 10000;
        PageRequest pageRequest = PageRequest.of(0, 10);
        System.out.println("=== productReviews ===");
        Page<Review> productReviews = reviewQueryService.selectReviewListOrderedBestWithProductId(productId, pageRequest);
        System.out.println("=== productReviews ===");

        int storeId = 10000;
        System.out.println("=== storeReviews ===");
        Page<Review> storeReviews = reviewQueryService.selectReviewListOrderedBestWithStoreId(storeId, pageRequest);
        System.out.println("=== storeReviews ===");
        // when

        // then
    }

    @DisplayName("sample test")
    @Test
    void test() {
        // given
        reviewRepository.findAllByProductIdAndIsDeletedFalseOrderByCreatedAtDesc(10000, PageRequest.of(1, 10));
        // when

        // then
    }
}