package com.matsinger.barofishserver.domain.review.application;

import com.matsinger.barofishserver.domain.review.domain.*;
import com.matsinger.barofishserver.domain.review.dto.v2.*;
import com.matsinger.barofishserver.domain.review.dto.ReviewStatistic;
import com.matsinger.barofishserver.domain.review.dto.ReviewTotalStatistic;
import com.matsinger.barofishserver.domain.review.repository.ReviewEvaluationRepository;
import com.matsinger.barofishserver.domain.review.repository.ReviewLikeRepository;
import com.matsinger.barofishserver.domain.review.repository.ReviewRepository;
import com.matsinger.barofishserver.domain.review.repository.ReviewRepositoryImpl;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class ReviewQueryService {
    private final ReviewRepository reviewRepository;
    private final ReviewEvaluationRepository evaluationRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final ReviewRepositoryImpl reviewRepositoryImpl;
    private final ReviewEvaluationRepository reviewEvaluationRepository;

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

    public Page<Review> selectAllReviewListExceptDeleted(Specification<Review> spec, PageRequest pageRequest) {
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
        return reviewRepository.findAllByProductIdAndIsDeletedFalseOrderByCreatedAtDesc(productId, pageRequest);
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

    public ProductReviewDto getPagedProductReviewInfo(Integer productId, Integer userId, ReviewOrderByType orderType, PageRequest pageRequest) {

        List<ReviewDtoV2> pagedProductReviews = reviewRepositoryImpl.getPagedProductReviews(productId, orderType, pageRequest);
        setReviewLike(pagedProductReviews, userId);

        Long totalReviewCount = reviewRepositoryImpl.getProductReviewCount(productId);

        PageImpl<ReviewDtoV2> pagedReviews = new PageImpl<>(pagedProductReviews, pageRequest, totalReviewCount);

        convertStringImageUrlsToArray(pagedReviews);

        List<ReviewEvaluationSummaryDto> productReviewEvaluations = reviewRepositoryImpl.getProductReviewEvaluations(productId);

        return ProductReviewDto.builder()
                .productId(productId)
                .reviewCount(totalReviewCount)
                .evaluationSummaryDtos(productReviewEvaluations)
                .pagedReviews(pagedReviews)
                .build();
    }

    public Review findById(Integer id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("후기를 찾을 수 없습니다."));
    }

    public StoreReviewDto getPagedProductSumStoreReviewInfo(Integer storeId, Integer userId, ReviewOrderByType orderType, PageRequest pageRequest) {
        Long totalReviewCount = reviewRepositoryImpl.getStoreProductReviewCount(storeId);
        List<ReviewDtoV2> pagedProductSumStoreReviews = reviewRepositoryImpl.getPagedProductSumStoreReviews(storeId, orderType, pageRequest);
        setReviewLike(pagedProductSumStoreReviews, userId);

        PageImpl<ReviewDtoV2> pagedReviews = new PageImpl<>(pagedProductSumStoreReviews, pageRequest, totalReviewCount);

        convertStringImageUrlsToArray(pagedReviews);

        List<ReviewEvaluationSummaryDto> productSumStoreReviewEvaluations = reviewRepositoryImpl.getProductSumStoreReviewEvaluations(storeId);

        return StoreReviewDto.builder()
                .storeId(storeId)
                .reviewCount(totalReviewCount)
                .evaluationSummaryDtos(productSumStoreReviewEvaluations)
                .pagedReviews(pagedReviews)
                .build();
    }

    public UserReviewDto getPagedUserReview(Integer userId, ReviewOrderByType orderType, PageRequest pageRequest) {

        Long totalReviewCount = reviewRepositoryImpl.getUserReviewCount(userId);
        List<ReviewDtoV2> pagedUserReviews = reviewRepositoryImpl.getPagedUserReview(userId, orderType, pageRequest);

        PageImpl<ReviewDtoV2> pagedReviews = new PageImpl<>(pagedUserReviews, pageRequest, totalReviewCount);
        convertStringImageUrlsToArray(pagedReviews);

        return UserReviewDto.builder()
                .userId(userId)
                .reviewCount(totalReviewCount)
                .pagedReviews(pagedReviews)
                .build();
    }

    private void setReviewLike(List<ReviewDtoV2> pagedReviewDto, Integer userId) {
        for (ReviewDtoV2 reviewDtoV2 : pagedReviewDto) {
            List<Integer> reviewLikeUserIdx = reviewRepositoryImpl.getReviewLikeUserIdx(reviewDtoV2.getReviewId());
            if (reviewLikeUserIdx.contains(userId)) {
                reviewDtoV2.setSameUserLike(true);
                break;
            }
            reviewDtoV2.setSameUserLike(false);
        }
    }

    private void convertStringImageUrlsToArray(PageImpl<ReviewDtoV2> pagedReviews) {
        for (ReviewDtoV2 pagedReview : pagedReviews) {
            String imageUrls = pagedReview.getImages();
            String processedUrls = imageUrls.substring(1, imageUrls.length() - 1);
            String[] parsedUrls = processedUrls.split(", ");

            pagedReview.setImageUrls(parsedUrls);

            if (!pagedReview.getProductImage().equals("[]") && !pagedReview.getProductImage().equals("")) {
                String[] parsedProductImage = pagedReview.getProductImage().split(",");
                String parsedProductImage2 = parsedProductImage[0];
                String parsedProductImage3 = parsedProductImage2.replace("\"", "");
                String parsedProductImage4 = parsedProductImage3.replace("[", "").replace("]", "");
                pagedReview.setProductImage(parsedProductImage4);
            }
            if (pagedReview.getProductImage() == "[]") {
                pagedReview.setProductImage(null);
            }
        }
    }

    public Page<AdminReviewDto> findAllReviewExceptDeleted(
            ReviewOrderBy orderBy, Sort.Direction sort, String orderId,
            String productName, String partnerName, String reviewer,
            String evaluation, Timestamp createdAtS, Timestamp createdAtE,
            Integer storeId, Pageable pageRequest) {

        List<AdminReviewDto> reviewDtos = reviewRepositoryImpl.findAllExceptDeleted(orderBy, sort, orderId,
                productName, partnerName, reviewer,
                evaluation, createdAtS, createdAtE,
                storeId, pageRequest);

        for (AdminReviewDto reviewDto : reviewDtos) {

            String imageUrls = reviewDto.getImages();
            String processedUrls = imageUrls.substring(1, imageUrls.length() - 1);
            String[] parsedUrls = processedUrls.split(", ");

            reviewDto.setImageUrls(parsedUrls);
            reviewDto.deleteImages();

            List<ReviewEvaluationType> evaluations = new ArrayList<>();
            List<ReviewEvaluation> findEvaluations = reviewEvaluationRepository.findAllByReviewId(reviewDto.getReviewId());
            for (ReviewEvaluation findEvaluation : findEvaluations) {
                evaluations.add(findEvaluation.getEvaluation());
            }
            reviewDto.setEvaluations(evaluations);
        }

        Long reviewCount = reviewRepositoryImpl.getAllReviewCountExceptDeleted();

        return new PageImpl<>(reviewDtos, pageRequest, reviewCount);
    }
}
