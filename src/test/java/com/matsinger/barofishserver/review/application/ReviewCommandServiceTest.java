package com.matsinger.barofishserver.review.application;

import com.matsinger.barofishserver.order.orderprductinfo.application.OrderProductInfoQueryService;
import com.matsinger.barofishserver.product.application.ProductQueryService;
import com.matsinger.barofishserver.review.domain.Review;
import com.matsinger.barofishserver.review.domain.ReviewEvaluation;
import com.matsinger.barofishserver.review.domain.ReviewEvaluationType;
import com.matsinger.barofishserver.review.repository.ReviewEvaluationRepository;
import com.matsinger.barofishserver.review.repository.ReviewRepository;
import com.matsinger.barofishserver.store.application.StoreQueryService;
import com.matsinger.barofishserver.user.application.UserQueryService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("local")
@Transactional
class ReviewCommandServiceTest {
    @Autowired private ReviewRepository reviewRepository;
    @Autowired private ReviewEvaluationRepository reviewEvaluationRepository;
    @Autowired private ReviewCommandService reviewCommandService;
    @Autowired private ProductQueryService productQueryService;
    @Autowired private StoreQueryService storeQueryService;
    @Autowired private UserQueryService userQueryService;
    @Autowired private OrderProductInfoQueryService orderProductInfoQueryService;

    private Review review1;
    private Review review2;

    @BeforeEach
    public void ReviewCommandServiceTest() {
        Review createdReview = reviewRepository.save(Review.builder()
                .productId(10000)
                .storeId(10000)
                .userId(10000)
                .orderProductInfoId(1)
                .images("[]")
                .content("test")
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
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
                .orderProductInfoId(1)
                .images("[]")
                .content("test")
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .isDeleted(false)
                .build());
        ReviewEvaluation createdReviewEvaluation2 = reviewEvaluationRepository.save(ReviewEvaluation.builder()
                .reviewId(createdReview2.getId())
                .evaluation(ReviewEvaluationType.TASTE)
                .build());

        this.review1 = createdReview;
        this.review2 = createdReview2;
    }

    @DisplayName("리뷰를 추가하고 상태를 변경할 수 있다.")
    @Test
    void 리뷰_추가_후_상태변경_테스트() {
        // given
        // when
        Boolean isDeleted = reviewCommandService.deleteReview(review1.getId());

        // then
        Assertions.assertThat(isDeleted).isEqualTo(true);
    }

    @DisplayName("리뷰의 상태가 삭제로 변경되면 리뷰 화면에서 보이지 않는다.")
    @Test
    void 상태가_삭제로_변경된_리뷰_불러오지_않는지_테스트() {
        // given

        // when

        // then
    }
}