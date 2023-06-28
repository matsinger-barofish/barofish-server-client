package com.matsinger.barofishserver.review;

import com.matsinger.barofishserver.order.OrderService;
import com.matsinger.barofishserver.order.object.OrderDto;
import com.matsinger.barofishserver.order.repository.OrderRepository;
import com.matsinger.barofishserver.review.object.*;
import com.matsinger.barofishserver.store.StoreService;
import com.matsinger.barofishserver.store.object.SimpleStore;
import com.matsinger.barofishserver.user.UserService;
import com.matsinger.barofishserver.user.object.UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewEvaluationRepository evaluationRepository;
    private final UserService userService;
    private final ReviewLikeRepository reviewLikeRepository;
    private final StoreService storeService;
    private final OrderRepository orderRepository;

    public List<ReviewEvaluationType> selectReviewEvaluations(Integer reviewId) {
        return evaluationRepository.findAllByReviewId(reviewId).stream().map(reviewEvaluation -> {
            return reviewEvaluation.getEvaluation();
        }).toList();
    }

    public Boolean checkLikeReview(Integer userId, Integer reviewId) {
        return reviewLikeRepository.existsByUserIdAndReviewId(userId, reviewId);
    }

    public Integer countReviewWithProductId(Integer productId) {
        return reviewRepository.countAllByProductId(productId);
    }

    public void likeReview(Integer userId, Integer reviewId) {
        reviewLikeRepository.save(ReviewLike.builder().userId(userId).reviewId(reviewId).build());
    }

    public void unlikeReview(Integer userId, Integer reviewId) {
        reviewLikeRepository.deleteById(ReviewLikeId.builder().userId(userId).reviewId(reviewId).build());
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

    public Page<Review> selectReviewListByProduct(Integer productId, Integer page, Integer take) {
        return reviewRepository.findAllByProductId(productId, Pageable.ofSize(take).withPage(page));
    }

    public ReviewDto convert2Dto(Review review) {
        ReviewDto dto = review.convert2Dto();
        UserInfo userDto = userService.selectUserInfo(review.getUserId());
        SimpleStore store = storeService.selectStoreInfo(review.getStore().getId()).convert2Dto();
        Integer likeCount = countReviewLike(review.getId());
//        OrderDto order = orderService.convert2Dto(orderService.selectOrder(review.getOrderId()));
//        dto.setOrder(order);
        dto.setLikeCount(likeCount);
        dto.setIsLike(false);
        dto.setLikeCount(0);
        dto.setUser(userDto.convert2Dto());
        dto.setStore(store);
        return dto;
    }

    public ReviewDto convert2Dto(Review review, Integer userId) {
        ReviewDto dto = review.convert2Dto();
        UserInfo userDto = userService.selectUserInfo(review.getUserId());
        SimpleStore store = storeService.selectStoreInfo(review.getStore().getId()).convert2Dto();
        Integer likeCount = countReviewLike(review.getId());
        dto.setIsLike(reviewLikeRepository.existsByUserIdAndReviewId(userId, review.getId()));
//        OrderDto order = orderService.convert2Dto(orderService.selectOrder(review.getOrderId()));
//        dto.setOrder(order);
        dto.setLikeCount(likeCount);
        dto.setIsLike(false);
        dto.setLikeCount(0);
        dto.setUser(userDto.convert2Dto());
        dto.setStore(store);
        return dto;
    }

    public Review selectReview(Integer reviewId) {
        return reviewRepository.findById(reviewId).orElseThrow(() -> {
            throw new Error("리뷰 정보를 찾을 수 없습니다.");
        });
    }

    public Integer countReviewLike(Integer reviewId) {
        return reviewLikeRepository.countAllByReviewId(reviewId);
    }

    public void deleteReviewsByUserId(Integer userId) {
        reviewRepository.deleteAllByUserId(userId);
    }

    public Review addReview(Review review) {
        return reviewRepository.save(review);
    }

    public Review updateReview(Review review) {
        return reviewRepository.save(review);
    }

    @Transactional
    public Boolean deleteReview(Integer reviewId) {
        try {
            evaluationRepository.deleteAllByReviewId(reviewId);
            reviewLikeRepository.deleteAllByReviewId(reviewId);
            reviewRepository.deleteById(reviewId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<ReviewStatistic> selectReviewStatisticsWithProductId(Integer productId) {
        return reviewRepository.selectReviewStatisticsWithProductId(productId).stream().map(tuple -> {
            return ReviewStatistic.builder().key(tuple.get("evaluation").toString()).count(Integer.valueOf(tuple.get(
                    "count").toString())).build();
        }).toList();
    }

    public List<ReviewStatistic> selectReviewStatisticsWithStoreId(Integer storeId) {
        return reviewRepository.selectReviewStatisticsWithStoreId(storeId).stream().map(tuple -> {
            return ReviewStatistic.builder().key(tuple.get("evaluation").toString()).count(Integer.valueOf(tuple.get(
                    "count").toString())).build();
        }).toList();
    }

    private Integer findByKey(List<ReviewStatistic> reviewStatistics, String key) {
        for (ReviewStatistic statistic : reviewStatistics) {
            if (statistic.getKey().equals(key)) return statistic.getCount();
        }
        return 0;
    }

    public ReviewTotalStatistic selectReviewTotalStatisticWithProductId(Integer productId) {
        List<ReviewStatistic> statistics = selectReviewStatisticsWithProductId(productId);
        ReviewTotalStatistic
                totalStatistic =
                ReviewTotalStatistic.builder().taste(findByKey(statistics, "TASTE")).fresh(findByKey(statistics,
                        "FRESH")).price(findByKey(statistics, "PRICE")).packaging(findByKey(statistics,
                        "PACKAGING")).size(findByKey(statistics, "SIZE")).build();
        return totalStatistic;
    }

    public Integer selectReviewLikeCount(Integer reviewId) {
        return reviewRepository.selectReviewLikeCountWithReviewId(reviewId);
    }

    public void deleteReviewWithReviewId(Integer reviewId) {
        evaluationRepository.deleteAllByReviewId(reviewId);
    }

    public List<ReviewEvaluation> addReviewEvaluationList(Integer reviewId, List<ReviewEvaluationType> types) {
        List<ReviewEvaluation> data = types.stream().map(reviewEvaluationType -> {
            return ReviewEvaluation.builder().reviewId(reviewId).evaluation(reviewEvaluationType).build();
        }).toList();
        return evaluationRepository.saveAll(data);
    }

    public Boolean checkReviewWritten(Integer userId, Integer productId, String orderId) {
        return reviewRepository.existsByUserIdAndProductIdAndOrderId(userId, productId, orderId);
    }
}
