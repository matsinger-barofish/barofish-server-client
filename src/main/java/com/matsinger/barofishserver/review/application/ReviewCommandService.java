package com.matsinger.barofishserver.review.application;

import com.matsinger.barofishserver.report.repository.ReportRepository;
import com.matsinger.barofishserver.review.domain.*;
import com.matsinger.barofishserver.review.dto.ReviewDto;
import com.matsinger.barofishserver.review.repository.ReviewEvaluationRepository;
import com.matsinger.barofishserver.review.repository.ReviewLikeRepository;
import com.matsinger.barofishserver.review.repository.ReviewRepository;
import com.matsinger.barofishserver.siteInfo.application.SiteInfoQueryService;
import com.matsinger.barofishserver.siteInfo.domain.SiteInformation;
import com.matsinger.barofishserver.store.application.StoreService;
import com.matsinger.barofishserver.store.dto.SimpleStore;
import com.matsinger.barofishserver.userinfo.domain.UserInfo;
import com.matsinger.barofishserver.userinfo.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class ReviewCommandService {
    private final ReviewRepository reviewRepository;
    private final ReviewEvaluationRepository evaluationRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final UserInfoRepository userInfoRepository;
    private final ReportRepository reportRepository;
    private final StoreService storeService;
    private final SiteInfoQueryService siteInfoQueryService;
    public void likeReview(Integer userId, Integer reviewId) {
        reviewLikeRepository.save(ReviewLike.builder().userId(userId).reviewId(reviewId).build());
    }

    public void unlikeReview(Integer userId, Integer reviewId) {
        reviewLikeRepository.deleteById(ReviewLikeId.builder().userId(userId).reviewId(reviewId).build());
    }
    public void increaseUserPoint(Integer userId, Boolean hasImage) {
        UserInfo userInfo = userInfoRepository.findById(userId).orElseThrow(() -> {
            throw new Error("유저 정보를 찾을 수 없습니다.");
        });
        String siteInfoId = hasImage ? "INT_REVIEW_POINT_TEXT" : "INT_REVIEW_POINT_IMAGE";
        SiteInformation siteInformation = siteInfoQueryService.selectSiteInfo(siteInfoId);
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
    public Integer countReviewLike(Integer reviewId) {
        return reviewLikeRepository.countAllByReviewId(reviewId);
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
}