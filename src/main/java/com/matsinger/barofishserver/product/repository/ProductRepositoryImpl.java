package com.matsinger.barofishserver.product.repository;

import com.matsinger.barofishserver.product.domain.ProductSortBy;
import com.matsinger.barofishserver.product.domain.ProductState;
import com.matsinger.barofishserver.product.dto.ProductListDto;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.matsinger.barofishserver.data.curation.domain.QCurationProductMap.curationProductMap;
import static com.matsinger.barofishserver.order.orderprductinfo.domain.QOrderProductInfo.orderProductInfo;
import static com.matsinger.barofishserver.product.domain.QProduct.product;
import static com.matsinger.barofishserver.product.optionitem.domain.QOptionItem.optionItem;
import static com.matsinger.barofishserver.review.domain.QReview.review;
import static com.matsinger.barofishserver.store.domain.QStoreInfo.storeInfo;
import static com.matsinger.barofishserver.category.domain.QCategory.category;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ProductListDto> getProducts(Pageable pageable, ProductSortBy sortBy, int userId) {

        OrderSpecifier[] orderSpecifiers = createProductSortSpecifier(sortBy);

        List<ProductListDto> results = queryFactory.select(
                        Projections.fields(ProductListDto.class,
                                product.id.as("id"),
                                product.state.as("state"),
                                product.images.as("image"),
                                product.title.as("title"),
                                product.needTaxation.as("isNeedTaxation"),
                                optionItem.discountPrice.as("discountPrice"),
                                optionItem.originPrice.as("originPrice"),
                                ExpressionUtils.as(Expressions.constant(0), "reviewCount"),
                                ExpressionUtils.as(Expressions.constant(false), "isLike"),
                                storeInfo.storeId.as("storeId"),
                                storeInfo.name.as("storeName"),
                                product.minOrderPrice.as("minOrderPrice"),
                                storeInfo.profileImage.as("storeImage"),
                                product.deliverFeeType.as("deliverFeeType"),
                                category.parentCategory.as("parentCategoryId"),
                                ExpressionUtils.as(Expressions.constant(new ArrayList<>()), "filterValues")
                        ))
                .from(product)
                .leftJoin(optionItem).on(optionItem.id.eq(product.representOptionItemId))
                .leftJoin(storeInfo).on(product.storeId.eq(storeInfo.storeId))
                .leftJoin(category).on(product.category.id.eq(category.id))
                .leftJoin(orderProductInfo).on(orderProductInfo.productId.eq(product.id))
                .leftJoin(review).on(review.productId.eq(product.id))
                .leftJoin(curationProductMap).on(curationProductMap.product.eq(product))
                .groupBy(product.id)
                .orderBy(orderSpecifiers)
                .where(product.state.eq(ProductState.ACTIVE),
                        review.isDeleted.eq(false))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long resultCount = results.size();

        return new PageImpl<>(results, pageable, resultCount);
    }

    private OrderSpecifier[] createProductSortSpecifier(ProductSortBy sortBy) {

        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();

        if (sortBy.equals(ProductSortBy.NEW)) {
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, product.createdAt));
        }
        if (sortBy.equals(ProductSortBy.SALES)) {
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, orderProductInfo.id.count()));
        }
        if (sortBy.equals(ProductSortBy.REVIEW)) {
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, review.id.count()));
        }
        if (sortBy.equals(ProductSortBy.LOW_PRICE)) {
            orderSpecifiers.add(new OrderSpecifier(Order.ASC, optionItem.discountPrice));
        }
        if (sortBy.equals(ProductSortBy.HIGH_PRICE)) {
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, optionItem.discountPrice));
        }
        return orderSpecifiers.toArray(new OrderSpecifier[orderSpecifiers.size()]);
    }
}
