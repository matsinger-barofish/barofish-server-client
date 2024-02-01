package com.matsinger.barofishserver.domain.review.repository;

import com.matsinger.barofishserver.domain.review.dto.ProductReviewPictureInquiryDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.matsinger.barofishserver.domain.product.domain.QProduct.product;
import static com.matsinger.barofishserver.domain.review.domain.QReview.review;

@Repository
@RequiredArgsConstructor
public class ReviewQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<ProductReviewPictureInquiryDto> getReviewsWhichPictureExists(Integer id, String type) {



        return queryFactory.select(Projections.fields(
                ProductReviewPictureInquiryDto.class,
                review.images.as("reviewPictureUrls"),
                review.id.as("reviewId")
        ))
                .from(product)
                .leftJoin(review).on(product.id.eq(review.productId)
                        .and(review.images.notLike("[]")))
                .where(eqProductOrStore(id, type)
                        .and(review.isDeleted.isFalse()))
                .limit(5)
                .fetch();
    }

    private BooleanExpression eqProductOrStore(Integer id, String type) {
        if (type.equals("product")) {
            return product.id.eq(id);
        }
        if (type.equals("store")) {
            return product.storeId.eq(id);
        }
        return product.id.eq(id);
    }
}
