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

import javax.annotation.PostConstruct;
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

    @Autowired
    private EntityManager em;

    private Review review1;
    private Review review2;

    @PostConstruct
    public void ReviewCommandServiceTest() {
        Review createdReview = reviewRepository.save(Review.builder()
                .productId(10001)
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
                .productId(10001)
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

    @DisplayName("리뷰의 상태가 삭제로 변경되면 리뷰 화면에서 보이지 않는다.")
    @Test
    void 상태가_삭제로_변경된_리뷰_불러오지_않는지_테스트() {
        // given
        review1.setIsDeleted(true);
//        reviewRepository.save(review1);

        PageRequest pageRequest = PageRequest.of(0, 10);
        // when
        Page<Review> reviews = reviewQueryService.findAllByProductIdAndIsDeletedOrderByCreatedAtDesc(10000, false, pageRequest);
        // then
        System.out.println("reviews = " + reviews.stream().toList());

        List<Review> reviews2 = reviewRepository.findAll();
        System.out.println("reviews2 = " + reviews2.stream().toList());
    }
}