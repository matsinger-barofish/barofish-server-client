package com.matsinger.barofishserver.domain.product.repository;

import com.matsinger.barofishserver.domain.product.domain.ProductSortBy;
import com.matsinger.barofishserver.domain.product.domain.ProductState;
import com.matsinger.barofishserver.domain.product.dto.ProductListDtoV2;
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

import static com.matsinger.barofishserver.domain.category.domain.QCategory.category;
import static com.matsinger.barofishserver.domain.data.curation.domain.QCurationProductMap.curationProductMap;
import static com.matsinger.barofishserver.domain.order.orderprductinfo.domain.QOrderProductInfo.orderProductInfo;
import static com.matsinger.barofishserver.domain.product.domain.QProduct.product;
import static com.matsinger.barofishserver.domain.product.optionitem.domain.QOptionItem.optionItem;
import static com.matsinger.barofishserver.domain.review.domain.QReview.review;
import static com.matsinger.barofishserver.domain.store.domain.QStoreInfo.storeInfo;


@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ProductListDtoV2> getProducts(Pageable pageable, ProductSortBy sortBy, String keyword) {

        OrderSpecifier[] orderSpecifiers = createProductSortSpecifier(sortBy);

        List<ProductListDtoV2> results = queryFactory.select(
                        Projections.fields(ProductListDtoV2.class,
                                product.id.as("id"),
                                product.state.as("state"),
                                product.images.as("image"),
                                product.title.as("title"),
                                product.needTaxation.as("isNeedTaxation"),
                                optionItem.discountPrice.as("discountPrice"),
                                optionItem.originPrice.as("originPrice"),
//                                ExpressionUtils.as(Expressions.constant(0), "reviewCount"),
                                ExpressionUtils.as(Expressions.constant(false), "isLike"),
                                storeInfo.storeId.as("storeId"),
                                storeInfo.name.as("storeName"),
                                product.minOrderPrice.as("minOrderPrice"),
                                storeInfo.profileImage.as("storeImage"),
                                product.deliverFeeType.as("deliverFeeType"),
                                category.parentCategory.id.as("parentCategoryId"),
                                ExpressionUtils.as(Expressions.constant(new ArrayList<>()), "filterValues"),
                                review.isDeleted.eq(false).count().as("reviewCount")
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
                .where(product.state.eq(ProductState.ACTIVE)
//                        review.isDeleted.eq(false),
                        .and(product.title.contains(keyword)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long resultCount = results.size();

        return new PageImpl<>(results, pageable, resultCount);
    }

//    @Override
//    public Page<ProductListDto> getProductsV2(Pageable pageable, ProductSortBy sortBy, String keyword) {
//
//        OrderSpecifier[] orderSpecifiers = createProductSortSpecifier(sortBy);
//
//        List<ProductListDto> results = queryFactory.select(
//                        )
//                .from(product)
//                .leftJoin(optionItem).on(optionItem.id.eq(product.representOptionItemId))
//                .leftJoin(storeInfo).on(product.storeId.eq(storeInfo.storeId))
//                .leftJoin(category).on(product.category.id.eq(category.id))
//                .leftJoin(orderProductInfo).on(orderProductInfo.productId.eq(product.id))
//                .leftJoin(review).on(review.productId.eq(product.id))
//                .leftJoin(curationProductMap).on(curationProductMap.product.eq(product))
//                .orderBy(orderSpecifiers)
//                .where(product.state.eq(ProductState.ACTIVE),
//                        review.isDeleted.eq(false),
//                        product.title.contains(keyword))
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .transform(
//                        groupBy(product.id)
//                                .list(
//                                        Projections.fields(ProductListDto.class,
//                                                product.id.as("id"),
//                                                product.state.as("state"),
//                                                product.images.as("image"),
//                                                product.title.as("title"),
//                                                product.needTaxation.as("isNeedTaxation"),
//                                                optionItem.discountPrice.as("discountPrice"),
//                                                optionItem.originPrice.as("originPrice"),
//                                                ExpressionUtils.as(Expressions.constant(0), "reviewCount"),
//                                                ExpressionUtils.as(Expressions.constant(false), "isLike"),
//                                                storeInfo.storeId.as("storeId"),
//                                                storeInfo.name.as("storeName"),
//                                                product.minOrderPrice.as("minOrderPrice"),
//                                                storeInfo.profileImage.as("storeImage"),
//                                                product.deliverFeeType.as("deliverFeeType"),
//                                                category.parentCategory.id.as("parentCategoryId"),
//                                                ExpressionUtils.as(Expressions.constant(new ArrayList<>()), "filterValues"),
//                                                review.isDeleted.eq(false).count().as("reviewCount")
//                                        )
//                                )
//                );
//
//        long resultCount = results.size();
//
//        return new PageImpl<>(results, pageable, resultCount);
//    }

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
