package com.matsinger.barofishserver.review.application;


import com.matsinger.barofishserver.report.repository.ReportRepository;
import com.matsinger.barofishserver.review.domain.*;
import com.matsinger.barofishserver.review.dto.ReviewDto;
import com.matsinger.barofishserver.review.dto.ReviewStatistic;
import com.matsinger.barofishserver.review.dto.ReviewTotalStatistic;
import com.matsinger.barofishserver.review.repository.ReviewEvaluationRepository;
import com.matsinger.barofishserver.review.repository.ReviewLikeRepository;
import com.matsinger.barofishserver.review.repository.ReviewRepository;
import com.matsinger.barofishserver.siteInfo.SiteInfoService;
import com.matsinger.barofishserver.siteInfo.SiteInformation;
import com.matsinger.barofishserver.store.application.StoreService;
import com.matsinger.barofishserver.store.dto.SimpleStore;
import com.matsinger.barofishserver.userinfo.domain.UserInfo;
import com.matsinger.barofishserver.userinfo.repository.UserInfoRepository;
import jakarta.persistence.Tuple;
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
    private final ReviewLikeRepository reviewLikeRepository;
    private final UserInfoRepository userInfoRepository;
    private final ReportRepository reportRepository;
    private final StoreService storeService;
    private final SiteInfoService siteInfoService;

    public List<ReviewEvaluationType> selectReviewEvaluations(Integer reviewId) {
        return evaluationRepository.findAllByReviewId(reviewId).stream().map(ReviewEvaluation::getEvaluation).toList();
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

    public void increaseUserPoint(Integer userId, Boolean hasImage) {
        UserInfo userInfo = userInfoRepository.findById(userId).orElseThrow(() -> {
            throw new Error("유저 정보를 찾을 수 없습니다.");
        });
        String siteInfoId = hasImage ? "INT_REVIEW_POINT_TEXT" : "INT_REVIEW_POINT_IMAGE";
        SiteInformation siteInformation = siteInfoService.selectSiteInfo(siteInfoId);
        Integer point = Integer.parseInt(siteInformation.getContent());
        Integer increasedPoint = userInfo.getPoint() + point;
        userInfo.setPoint(increasedPoint);
        userInfoRepository.save(userInfo);
    }

    public ReviewDto convert2Dto(Review review) {
        ReviewDto dto = review.convert2Dto();
        UserInfo userInfo = userInfoRepository.findById(review.getUserId()).orElseThrow(() -> {
            throw new Error("유저 정보를 찾을 수 없습니다.");
        });
        SimpleStore
                store =
                storeService.convert2SimpleDto(storeService.selectStoreInfo(review.getStore().getId()), null);
        Integer likeCount = countReviewLike(review.getId());

        dto.setLikeCount(likeCount);
        dto.setIsLike(false);
        dto.setLikeCount(0);
        dto.setUser(userInfo.convert2Dto());
        dto.setStore(store);
        return dto;
    }

    public ReviewDto convert2Dto(Review review, Integer userId) {
        ReviewDto dto = review.convert2Dto();
        UserInfo userDto = userInfoRepository.findById(review.getUserId()).orElseThrow(() ->
                new Error("유저 정보를 찾을 수 없습니다."));
        SimpleStore store = storeService.selectStoreInfo(review.getStore().getId()).convert2Dto();
        Integer likeCount = countReviewLike(review.getId());
        dto.setIsLike(userId != null ? reviewLikeRepository.existsByUserIdAndReviewId(userId, review.getId()) : false);
        dto.setLikeCount(likeCount);
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
            reportRepository.deleteAllByReviewId(reviewId);
            evaluationRepository.deleteAllByReviewId(reviewId);
            reviewLikeRepository.deleteAllByReviewId(reviewId);
            reviewRepository.deleteById(reviewId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<ReviewStatistic> selectReviewStatisticsWithStoreId(Integer storeId) {
        return reviewRepository.selectReviewStatisticsWithStoreId(storeId).stream().map(tuple -> ReviewStatistic.builder().key(
                tuple.get("evaluation").toString()).count(Integer.valueOf(tuple.get("count").toString())).build()).toList();
    }

    public ReviewTotalStatistic selectReviewTotalStatisticWithProductId(Integer productId) {
        List<ReviewStatistic> statistics = selectReviewStatisticsWithProductId(productId);

        return ReviewTotalStatistic.builder()
                .taste(findByKey(statistics, "TASTE"))
                .fresh(findByKey(statistics, "FRESH"))
                .price(findByKey(statistics, "PRICE"))
                .packaging(findByKey(statistics, "PACKAGING"))
                .size(findByKey(statistics, "SIZE")).build();
    }

    private List<ReviewStatistic> selectReviewStatisticsWithProductId(Integer productId) {
        List<Tuple> tuples = reviewRepository.selectReviewStatisticsWithProductId(productId);
        return tuples.stream()
                .map(tuple -> {
                    return ReviewStatistic.builder()
                            .key(tuple.get("evaluation").toString())
                            .count(Integer.valueOf(tuple.get("count").toString()))
                            .build();
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

    public void deleteReviewWithReviewId(Integer reviewId) {
        evaluationRepository.deleteAllByReviewId(reviewId);
    }

    public void addReviewEvaluationList(Integer reviewId, List<ReviewEvaluationType> types) {
        List<ReviewEvaluation>
                data =
                types.stream().map(reviewEvaluationType -> ReviewEvaluation.builder().reviewId(reviewId).evaluation(
                        reviewEvaluationType).build()).toList();
        evaluationRepository.saveAll(data);
    }

    public Boolean checkReviewWritten(Integer userId, Integer productId, Integer orderProductInfoId) {
        return reviewRepository.existsByUserIdAndProductIdAndOrderProductInfoId(userId, productId, orderProductInfoId);
    }
}
