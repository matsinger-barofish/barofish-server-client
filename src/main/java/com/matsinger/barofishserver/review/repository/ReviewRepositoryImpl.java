package com.matsinger.barofishserver.review.repository;

import com.matsinger.barofishserver.review.domain.QReviewLike;
import com.matsinger.barofishserver.review.domain.Review;
import com.matsinger.barofishserver.review.domain.ReviewOrderByType;
import com.matsinger.barofishserver.review.dto.v2.ProductReviewDto;
import com.matsinger.barofishserver.review.dto.v2.ReviewDtoV2;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.NullExpression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.matsinger.barofishserver.product.domain.QProduct.product;
import static com.matsinger.barofishserver.review.domain.QReview.review;
import static com.matsinger.barofishserver.review.domain.QReviewLike.*;
import static com.matsinger.barofishserver.user.domain.QUser.user;
import static com.matsinger.barofishserver.userinfo.domain.QUserInfo.userInfo;
import static com.querydsl.core.QueryModifiers.offset;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ReviewDtoV2> getReviewsWithProduct(Integer productId, ReviewOrderByType orderType, Pageable pageable) {

        orderByCreatedAt(orderType);

        List<ReviewDtoV2> results = queryFactory
                .select(Projections.fields(ReviewDtoV2.class,

                        ))
                .from(review)
                .leftJoin(userInfo).on(userInfo.userId.eq(review.userId))
                .leftJoin(product).on(product.id.eq(review.productId))
                .leftJoin(
                        JPAExpressions
                                .select(reviewLike.reviewId, reviewLike.reviewId.count().as("likeSum"))
                                .from(reviewLike)
                                .groupBy(reviewLike.reviewId), reviewLike
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(results, pageable, results.size());
    }

    private OrderSpecifier[] createProductReviewOrderSpecifier(ReviewOrderByType orderType) {

        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();

        if (orderType.equals(ReviewOrderByType.RECENT)) {
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, review.createdAt));
        }

        if (orderType.equals(ReviewOrderByType.BEST)) {
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, ))
        }
    }
}
