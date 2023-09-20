package com.matsinger.barofishserver.review.repository;

import com.matsinger.barofishserver.review.domain.ReviewOrderByType;
import com.matsinger.barofishserver.review.dto.v2.ReviewDtoV2;
import com.matsinger.barofishserver.review.dto.v2.ReviewEvaluationSummaryDto;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.matsinger.barofishserver.product.domain.QProduct.product;
import static com.matsinger.barofishserver.review.domain.QReview.review;
import static com.matsinger.barofishserver.review.domain.QReviewEvaluation.reviewEvaluation;
import static com.matsinger.barofishserver.review.domain.QReviewLike.*;
import static com.matsinger.barofishserver.userinfo.domain.QUserInfo.userInfo;
import static com.matsinger.barofishserver.grade.domain.QGrade.grade;
import static com.querydsl.core.group.GroupBy.groupBy;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ReviewDtoV2> getPagedProductReviews(int productId, ReviewOrderByType orderCondition, Pageable pageable) {
        OrderSpecifier[] orderSpecifiers = createProductReviewOrderSpecifier(orderCondition);

        return queryFactory
                .select(
                        Projections.fields(ReviewDtoV2.class,
                                userInfo.userId.as("userId"),
                                userInfo.name.as("userName"),
                                grade.name.as("userGrade"),
                                product.title.as("productName"),
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
                .where(product.id.eq(productId), review.isDeleted.eq(false))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .groupBy(review.id)
                .orderBy(orderSpecifiers)
                .fetch();
    }

    public Long getProductReviewCount(Integer productId) {
        Long reviewCount = queryFactory
                .select(review.count())
                .from(review)
                .where(product.id.eq(productId), review.isDeleted.eq(false))
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
                .where(product.id.eq(productId), review.isDeleted.eq(false))
                .groupBy(product.id, reviewEvaluation.evaluation)
                .fetch();
    }

    private OrderSpecifier[] createProductReviewOrderSpecifier(ReviewOrderByType orderType) {

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
