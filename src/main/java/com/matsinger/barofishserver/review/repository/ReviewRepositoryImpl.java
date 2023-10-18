package com.matsinger.barofishserver.review.repository;

import com.matsinger.barofishserver.product.domain.ProductState;
import com.matsinger.barofishserver.review.domain.ReviewOrderByType;
import com.matsinger.barofishserver.review.dto.v2.ReviewDtoV2;
import com.matsinger.barofishserver.review.dto.v2.ReviewEvaluationSummaryDto;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.matsinger.barofishserver.order.orderprductinfo.domain.QOrderProductInfo.orderProductInfo;
import static com.matsinger.barofishserver.product.domain.QProduct.product;
import static com.matsinger.barofishserver.product.optionitem.domain.QOptionItem.optionItem;
import static com.matsinger.barofishserver.review.domain.QReview.review;
import static com.matsinger.barofishserver.review.domain.QReviewEvaluation.reviewEvaluation;
import static com.matsinger.barofishserver.review.domain.QReviewLike.*;
import static com.matsinger.barofishserver.store.domain.QStoreInfo.storeInfo;
import static com.matsinger.barofishserver.userinfo.domain.QUserInfo.userInfo;
import static com.matsinger.barofishserver.grade.domain.QGrade.grade;
import static com.querydsl.core.group.GroupBy.groupBy;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ReviewDtoV2> getPagedProductReviews(int productId, ReviewOrderByType orderCondition, Pageable pageable) {
        OrderSpecifier[] orderSpecifiers = createReviewOrderSpecifier(orderCondition);

        return queryFactory
                .select(
                        Projections.fields(ReviewDtoV2.class,
                                userInfo.userId.as("userId"),
                                userInfo.name.as("userName"),
                                userInfo.nickname.as("userNickname"),
                                grade.name.as("userGrade"),
                                storeInfo.name.as("storeName"),
                                product.id.as("productId"),
                                product.title.as("productName"),
                                product.images.as("productImage"),
                                optionItem.originPrice.as("originPrice"),
                                optionItem.discountPrice.as("discountPrice"),
                                review.id.as("reviewId"),
                                review.content.as("reviewContent"),
                                review.createdAt.as("createdAt"),
                                review.images.as("images"),
                                reviewLike.reviewId.count().as("likeSum")
                        ))
                .from(review)
                .leftJoin(reviewLike).on(review.id.eq(reviewLike.reviewId))
                .leftJoin(userInfo).on(review.userId.eq(userInfo.userId))
                .leftJoin(grade).on(grade.eq(userInfo.grade))
                .leftJoin(product).on(product.id.eq(review.productId))
                .leftJoin(storeInfo).on(product.storeId.eq(storeInfo.storeId))
                .leftJoin(orderProductInfo).on(orderProductInfo.id.eq(review.orderProductInfoId))
                .leftJoin(optionItem).on(orderProductInfo.optionItemId.eq(optionItem.id))
                .where(review.id.eq(productId)
                        .and(review.isDeleted.eq(false)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .groupBy(review.id, reviewLike.reviewId)
                .orderBy(orderSpecifiers)
                .fetch();
    }

    public Long getProductReviewCount(Integer productId) {
        Long reviewCount = queryFactory
                .select(review.count())
                .from(review)
                .where(product.id.eq(productId)
                        .and(review.isDeleted.eq(false)))
                .fetchOne();

        return reviewCount;
    }

    @Override
    public List<ReviewEvaluationSummaryDto> getProductReviewEvaluations(int productId) {


        return queryFactory
                .select(Projections.fields(
                        ReviewEvaluationSummaryDto.class,
                        reviewEvaluation.evaluation.as("evaluationType"),
                        reviewEvaluation.evaluation.count().as("evaluationSum")
                ))
                .from(review)
                .leftJoin(product).on(product.id.eq(review.product.id))
                .leftJoin(reviewLike).on(reviewLike.reviewId.eq(review.id))
                .leftJoin(reviewEvaluation).on(reviewEvaluation.reviewId.eq(review.id))
                .where(product.id.eq(productId)
                        .and(review.isDeleted.eq(false)))
                .groupBy(product.id, reviewEvaluation.evaluation)
                .fetch();
    }



    @Override
    public List<ReviewDtoV2> getPagedProductSumStoreReviews(Integer storeId, ReviewOrderByType orderCondition, Pageable pageable) {
        OrderSpecifier[] orderSpecifiers = createReviewOrderSpecifier(orderCondition);

        return queryFactory
                .select(Projections.fields(
                        ReviewDtoV2.class,
                        userInfo.userId.as("userId"),
                        userInfo.name.as("userName"),
                        grade.name.as("userGrade"),
                        storeInfo.name.as("storeName"),
                        product.id.as("productId"),
                        product.title.as("productName"),
                        product.images.as("productImage"),
                        optionItem.originPrice.as("originPrice"),
                        optionItem.discountPrice.as("discountPrice"),
                        review.id.as("reviewId"),
                        review.content.as("reviewContent"),
                        review.createdAt.as("createdAt"),
                        review.images.as("images"),
                        reviewLike.reviewId.count().as("likeSum")
                )).from(review)
                .leftJoin(storeInfo).on(storeInfo.storeId.eq(review.storeId))
                .leftJoin(reviewLike).on(review.id.eq(reviewLike.reviewId))
                .leftJoin(userInfo).on(review.userId.eq(userInfo.userId))
                .leftJoin(grade).on(userInfo.grade.eq(grade))
                .leftJoin(product).on(product.id.eq(review.productId))
                .leftJoin(orderProductInfo).on(orderProductInfo.id.eq(review.orderProductInfoId))
                .leftJoin(optionItem).on(orderProductInfo.optionItemId.eq(optionItem.id))
                .where(review.storeId.eq(storeId)
                        .and(product.state.eq(ProductState.ACTIVE))
                        .and(review.isDeleted.eq(false)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .groupBy(review.id, reviewLike.reviewId)
                .orderBy(orderSpecifiers)
                .fetch();
    }

    public Long getStoreProductReviewCount(Integer storeId) {
        Long reviewCount = queryFactory
                .select(review.count())
                .from(review)
                .leftJoin(product).on(review.productId.eq(product.id))
                .where(review.storeId.eq(storeId)
                        .and(review.isDeleted.eq(false))
                        .and(product.state.eq(ProductState.ACTIVE)))
                .fetchOne();

        return reviewCount;
    }

    @Override
    public List<ReviewEvaluationSummaryDto> getProductSumStoreReviewEvaluations(Integer storeId) {

        return queryFactory
                .select(Projections.fields(
                        ReviewEvaluationSummaryDto.class,
                        reviewEvaluation.evaluation.as("evaluationType"),
                        reviewEvaluation.evaluation.count().as("evaluationSum")
                ))
                .from(storeInfo)
                .leftJoin(product).on(product.storeId.eq(storeInfo.storeId))
                .leftJoin(review).on(review.productId.eq(product.id))
                .leftJoin(reviewEvaluation).on(review.id.eq(reviewEvaluation.reviewId))
                .where(storeInfo.storeId.eq(storeId)
                        .and(product.state.eq(ProductState.ACTIVE))
                        .and(review.isDeleted.eq(false)))
                .groupBy(reviewEvaluation.evaluation)
                .fetch();
    }

    @Override
    public Long getUserReviewCount(Integer userId) {

        return queryFactory
                .select(review.count())
                .from(review)
                .leftJoin(product).on(review.productId.eq(product.id))
                .where(review.userId.eq(userId)
                        .and(product.state.eq(ProductState.ACTIVE))
                        .and(review.isDeleted.eq(false)))
                .fetchOne();
    }

    @Override
    public List<ReviewDtoV2> getPagedUserReview(Integer userId, ReviewOrderByType orderType, PageRequest pageRequest) {
        OrderSpecifier[] reviewOrderSpecifier = createReviewOrderSpecifier(orderType);

        return queryFactory
                .select(Projections.fields(
                        ReviewDtoV2.class,
                        userInfo.userId.as("userId"),
                        userInfo.name.as("userName"),
                        userInfo.nickname.as("userNickname"),
                        grade.name.as("userGrade"),
                        storeInfo.name.as("storeName"),
                        product.id.as("productId"),
                        product.title.as("productName"),
                        product.images.as("productImage"),
                        optionItem.originPrice.as("originPrice"),
                        optionItem.discountPrice.as("discountPrice"),
                        review.id.as("reviewId"),
                        review.content.as("reviewContent"),
                        review.createdAt.as("createdAt"),
                        review.images.as("images"),
                        reviewLike.reviewId.count().as("likeSum")
                ))
                .from(review)
                .leftJoin(reviewLike).on(review.id.eq(reviewLike.reviewId))
                .leftJoin(userInfo).on(userInfo.userId.eq(userId))
                .leftJoin(product).on(product.id.eq(review.productId))
                .leftJoin(storeInfo).on(product.storeId.eq(storeInfo.storeId))
                .leftJoin(orderProductInfo).on(orderProductInfo.id.eq(review.orderProductInfoId))
                .leftJoin(optionItem).on(orderProductInfo.optionItemId.eq(optionItem.id))
                .leftJoin(grade).on(userInfo.grade.eq(grade))
                .where(review.userId.eq(userId)
                        .and(product.state.eq(ProductState.ACTIVE))
                        .and(review.isDeleted.eq(false)))
                .groupBy(review.id, reviewLike.reviewId)
                .orderBy(reviewOrderSpecifier)
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetch();
    }

    private OrderSpecifier[] createReviewOrderSpecifier(ReviewOrderByType orderType) {

        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();

        if (orderType.equals(ReviewOrderByType.RECENT)) {
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, review.createdAt));
        }

        if (orderType.equals(ReviewOrderByType.BEST)) {
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, reviewLike.reviewId.count()));
        }
        return orderSpecifiers.toArray(new OrderSpecifier[orderSpecifiers.size()]);
    }
}
