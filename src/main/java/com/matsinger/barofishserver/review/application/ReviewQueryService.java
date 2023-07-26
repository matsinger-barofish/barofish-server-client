package com.matsinger.barofishserver.review.application;

import com.matsinger.barofishserver.review.domain.Review;
import com.matsinger.barofishserver.review.domain.ReviewEvaluation;
import com.matsinger.barofishserver.review.domain.ReviewEvaluationType;
import com.matsinger.barofishserver.review.dto.ReviewStatistic;
import com.matsinger.barofishserver.review.dto.ReviewTotalStatistic;
import com.matsinger.barofishserver.review.repository.ReviewEvaluationRepository;
import com.matsinger.barofishserver.review.repository.ReviewLikeRepository;
import com.matsinger.barofishserver.review.repository.ReviewRepository;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class ReviewQueryService {
    private final ReviewRepository reviewRepository;
    private final ReviewEvaluationRepository evaluationRepository;
    private final ReviewLikeRepository reviewLikeRepository;

    public List<ReviewEvaluationType> selectReviewEvaluations(Integer reviewId) {
        return evaluationRepository.findAllByReviewId(reviewId).stream().map(ReviewEvaluation::getEvaluation).toList();
    }

    public Boolean checkLikeReview(Integer userId, Integer reviewId) {
        return reviewLikeRepository.existsByUserIdAndReviewId(userId, reviewId);
    }

    public Integer countReviewWithProductId(Integer productId) {
        return reviewRepository.countAllByProductId(productId);
    }

    public Integer countAllReviewByUserId(Integer userId) {
        return reviewRepository.countAllByUserId(userId);
    }

    public Page<Review> selectAllReviewListByUserId(Integer userId, Integer page, Integer take) {
        return reviewRepository.findAllByUserId(userId, Pageable.ofSize(take).withPage(page));
    }

    public Page<Review> selectAllReviewList(Specification<Review> spec, PageRequest pageRequest) {
        return reviewRepository.findAll(spec, pageRequest);
    }

    public Page<Review> selectReviewListByStore(Integer storeId, PageRequest pageRequest) {
        return reviewRepository.findAllByStoreId(storeId, pageRequest);
    }

    public Page<Review> selectReviewListByStoreOrderedRecent(Integer storeId, PageRequest pageRequest) {
        return reviewRepository.findAllByStoreIdOrderByCreatedAtDesc(storeId, pageRequest);
    }

    public Page<Review> selectReviewListOrderedBestWithStoreId(Integer storeId, PageRequest pageRequest) {
        return reviewRepository.selectReviewOrderedBestWithStoreId(storeId, pageRequest);
    }

    public Page<Review> selectReviewListOrderedBestWithProductId(Integer productId, PageRequest pageRequest) {
        return reviewRepository.selectReviewOrderedBestWithProductId(productId, pageRequest);
    }

    public Page<Review> selectReviewListByProduct(Integer productId, PageRequest pageRequest) {
        return reviewRepository.findAllByProductIdOrderByCreatedAtDesc(productId, pageRequest);
    }

    public Review selectReview(Integer reviewId) {
        return reviewRepository.findById(reviewId).orElseThrow(() -> {
            throw new Error("리뷰 정보를 찾을 수 없습니다.");
        });
    }

    public List<ReviewStatistic> selectReviewStatisticsWithStoreId(Integer storeId) {
        return reviewRepository.selectReviewStatisticsWithStoreId(storeId).stream().map(tuple -> ReviewStatistic.builder().key(
                tuple.get("evaluation").toString()).count(Integer.valueOf(tuple.get("count").toString())).build()).toList();
    }

    public ReviewTotalStatistic selectReviewTotalStatisticWithProductId(Integer productId) {
        List<ReviewStatistic> statistics = selectReviewStatisticsWithProductId(productId);

        return ReviewTotalStatistic.builder().taste(findByKey(statistics, "TASTE")).fresh(findByKey(statistics,
                "FRESH")).price(findByKey(statistics, "PRICE")).packaging(findByKey(statistics, "PACKAGING")).size(
                findByKey(statistics, "SIZE")).build();
    }

    private List<ReviewStatistic> selectReviewStatisticsWithProductId(Integer productId) {
        List<Tuple> tuples = reviewRepository.selectReviewStatisticsWithProductId(productId);
        return tuples.stream().map(tuple -> {
            return ReviewStatistic.builder().key(tuple.get("evaluation").toString()).count(Integer.valueOf(tuple.get(
                    "count").toString())).build();
        }).toList();
    }

    private Integer findByKey(List<ReviewStatistic> reviewStatistics, String key) {
        for (ReviewStatistic statistic : reviewStatistics) {
            if (statistic.getKey().equals(key)) {
                return statistic.getCount();
            }
        }
        return 0;
    }

    public Integer selectReviewLikeCount(Integer reviewId) {
        return reviewRepository.selectReviewLikeCountWithReviewId(reviewId);
    }

    public Boolean checkReviewWritten(Integer userId, Integer productId, Integer orderProductInfoId) {
        return reviewRepository.existsByUserIdAndProductIdAndOrderProductInfoId(userId, productId, orderProductInfoId);
    }
}
