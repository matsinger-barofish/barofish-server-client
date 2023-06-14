package com.matsinger.barofishserver.review;

import com.matsinger.barofishserver.store.StoreService;
import com.matsinger.barofishserver.store.object.SimpleStore;
import com.matsinger.barofishserver.user.UserService;
import com.matsinger.barofishserver.user.object.UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
//    private final ProductService productService;
    private final UserService userService;
    private final StoreService storeService;


    public Page<Review> selectAllReviewList(Integer page, Integer take) {
        return reviewRepository.findAll(Pageable.ofSize(take).withPage(page));
    }

    public Page<Review> selectReviewListByStore(Integer storeId,Integer page, Integer take) {
        return reviewRepository.findAllByStoreId(storeId,Pageable.ofSize(take).withPage(page));
    }

    public Page<Review> selectReviewListByProduct(Integer productId, Integer page, Integer take) {
        return reviewRepository.findAllByProductId(productId,Pageable.ofSize(take).withPage(page));
    }

    public ReviewDto convert2Dto(Review review) {
        ReviewDto dto = review.convert2Dto();
//        ProductListDto productListDto = productService.selectProduct(review.getProduct().getId()).convert2ListDto();
        UserInfo userDto = userService.selectUserInfo(review.getUserId());
        SimpleStore store = storeService.selectStoreInfo(review.getStore().getId()).convert2Dto();
//        dto.setSimpleProduct(productListDto);
        dto.setUser(userDto);
        dto.setStore(store);
        return dto;
    }

    public Review selectReview(Integer reviewId) {
        return reviewRepository.findById(reviewId).orElseThrow(() -> {
            throw new Error("리뷰 정보를 찾을 수 없습니다.");
        });
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

    public Boolean deleteReview(Integer reviewId) {
        try {
            reviewRepository.deleteById(reviewId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<ReviewStatistic> selectReviewStatisticsWithProductId(Integer productId) {
        return reviewRepository.selectReviewStatisticsWithProductId(productId);
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
}
