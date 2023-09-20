package com.matsinger.barofishserver.review.repository;

import com.matsinger.barofishserver.review.domain.ReviewOrderByType;
import com.matsinger.barofishserver.review.dto.v2.ReviewDtoV2;
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

import static com.matsinger.barofishserver.review.domain.QReview.review;
import static com.matsinger.barofishserver.review.domain.QReviewLike.*;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ReviewDtoV2> getReviewsWithProduct(Integer productId, ReviewOrderByType orderType, Pageable pageable) {
        return null;
    }

    @Override
    public List<ReviewDtoV2> getProductReviews(int productId, ReviewOrderByType orderCondition) {
        OrderSpecifier[] orderSpecifiers = createProductReviewOrderSpecifier(orderCondition);

        return queryFactory
                .select(
                        Projections.fields(ReviewDtoV2.class,
                                reviewLike.reviewId.count().as("likeSum"),
                                review.content.as("reviewContent")
                ))
                .from(review)
                .leftJoin(reviewLike).on(review.id.eq(reviewLike.reviewId))
                .groupBy(review.id)
                .orderBy(orderSpecifiers)
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
