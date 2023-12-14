package com.matsinger.barofishserver.domain.product.application;

import com.matsinger.barofishserver.domain.order.orderprductinfo.domain.OrderProductState;
import com.matsinger.barofishserver.domain.product.domain.ProductState;
import com.matsinger.barofishserver.domain.product.dto.ProductListDto;
import com.matsinger.barofishserver.domain.searchFilter.domain.SearchFilterField;
import com.matsinger.barofishserver.domain.searchFilter.repository.SearchFilterFieldRepository;
import com.matsinger.barofishserver.domain.searchFilter.repository.SearchFilterRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.matsinger.barofishserver.domain.category.domain.QCategory.category;
import static com.matsinger.barofishserver.domain.data.curation.domain.QCurationProductMap.curationProductMap;
import static com.matsinger.barofishserver.domain.order.domain.QOrders.orders;
import static com.matsinger.barofishserver.domain.order.orderprductinfo.domain.QOrderProductInfo.orderProductInfo;
import static com.matsinger.barofishserver.domain.product.domain.QProduct.product;
import static com.matsinger.barofishserver.domain.product.optionitem.domain.QOptionItem.optionItem;
import static com.matsinger.barofishserver.domain.review.domain.QReview.review;
import static com.matsinger.barofishserver.domain.searchFilter.domain.QProductSearchFilterMap.productSearchFilterMap;
import static com.matsinger.barofishserver.domain.searchFilter.domain.QSearchFilter.searchFilter;
import static com.matsinger.barofishserver.domain.searchFilter.domain.QSearchFilterField.searchFilterField;
import static com.matsinger.barofishserver.domain.store.domain.QStoreInfo.storeInfo;
import static com.matsinger.barofishserver.domain.userinfo.domain.QUserInfo.userInfo;

@Repository
@RequiredArgsConstructor
public class ProductQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final SearchFilterFieldRepository searchFilterFieldRepository;
    private final SearchFilterRepository searchFilterRepository;

    public PageImpl<ProductListDto> selectNewerProducts(PageRequest pageRequest, List<Integer> categoryIds,
                                                    List<Integer> filterFieldsIds, Integer curationId,
                                                    String keyword, Integer storeId) {
        OrderSpecifier[] orderSpecifiers = createNewerSpecifier();

        Integer count = countNewerProducts(categoryIds, filterFieldsIds, curationId, keyword, storeId);

        List<ProductListDto> inquiryData = queryFactory
                .select(Projections.fields(
                        ProductListDto.class,
                        product.id.as("id"),
                        product.state.as("state"),
                        product.images.as("image"),
                        product.title.as("title"),
                        product.needTaxation.as("isNeedTaxation"),
                        optionItem.discountPrice.as("discountPrice"),
                        optionItem.originPrice.as("originPrice"),
                        review.id.countDistinct().intValue().as("reviewCount"),
                        storeInfo.storeId.as("storeId"),
                        storeInfo.name.as("storeName"),
                        product.minOrderPrice.as("minOrderPrice"),
                        storeInfo.profileImage.as("storeImage"),
                        product.deliverFeeType.as("delieverFeeType"),
                        category.parentCategory.id.as("parentCategoryId")
                ))
                .from(product)
                .leftJoin(storeInfo).on(product.storeId.eq(storeInfo.storeId))
                .leftJoin(optionItem).on(product.representOptionItemId.eq(optionItem.id))
                .leftJoin(orderProductInfo).on(product.id.eq(orderProductInfo.productId)
                        .and(orderProductInfo.state.in(
                                OrderProductState.PAYMENT_DONE, OrderProductState.FINAL_CONFIRM)))
                .leftJoin(orders).on(orders.id.eq(orderProductInfo.orderId))
                .leftJoin(userInfo).on(userInfo.userId.eq(orders.userId)
                        .and(
                                userInfo.email.notLike("baroTastingNote")
                                .and(userInfo.email.notLike("baroReviewId"))
                        )
                )
                .leftJoin(category).on(category.id.eq(product.category.id))
                .leftJoin(productSearchFilterMap).on(product.id.eq(productSearchFilterMap.productId))
                .leftJoin(searchFilterField).on(productSearchFilterMap.fieldId.eq(searchFilterField.id))
                .leftJoin(searchFilter).on(searchFilterField.searchFilterId.eq(searchFilter.id))
                .leftJoin(review).on(orderProductInfo.productId.eq(review.productId).and(review.isDeleted.eq(false)))
                .where(product.state.eq(ProductState.ACTIVE),
                        eqCuration(curationId),
                        isPromotionInProgress(),
                        eqStore(storeId),
                        isProductTitleLikeKeyword(keyword),
                        isIncludedCategory(categoryIds),
                        isIncludedSearchFilter(filterFieldsIds)
                )
                .groupBy(product.id)
                .orderBy(orderSpecifiers)
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetch();

        return new PageImpl<>(inquiryData, pageRequest, count);
    }

    public Integer countNewerProducts(List<Integer> categoryIds, List<Integer> filterFieldsIds, Integer curationId, String keyword, Integer storeId) {
        Integer count = (int) queryFactory.select(product.count())
                .from(product)
                .leftJoin(productSearchFilterMap).on(product.id.eq(productSearchFilterMap.productId))
                .leftJoin(searchFilterField).on(productSearchFilterMap.fieldId.eq(searchFilterField.id))
                .leftJoin(searchFilter).on(searchFilterField.searchFilterId.eq(searchFilter.id))
                .leftJoin(review).on(product.id.eq(review.productId))
                .where(product.state.eq(ProductState.ACTIVE),
                        eqCuration(curationId),
                        isPromotionInProgress(),
                        eqStore(storeId),
                        isProductTitleLikeKeyword(keyword),
                        isIncludedCategory(categoryIds),
                        isIncludedSearchFilter(filterFieldsIds)
                )
                .groupBy(product.id)
                .stream().count();
        return count;
    }

    private OrderSpecifier[] createNewerSpecifier() {
        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();

        orderSpecifiers.add(new OrderSpecifier(
                Order.DESC, product.createdAt
        ));
        return orderSpecifiers.toArray(new OrderSpecifier[orderSpecifiers.size()]);
    }

    public PageImpl<ProductListDto> selectPopularProducts(PageRequest pageRequest, List<Integer> categoryIds,
                                               List<Integer> filterFieldsIds, Integer curationId,
                                               String keyword, Integer storeId) {
        int count = countPopularProducts(categoryIds, filterFieldsIds, curationId, keyword, storeId);

        OrderSpecifier[] orderSpecifiers = createPopularOrderSpecifier();

        List<ProductListDto> inquiryData = queryFactory
                .select(Projections.fields(
                        ProductListDto.class,
                        product.id.as("id"),
                        product.state.as("state"),
                        product.images.as("image"),
                        product.title.as("title"),
                        product.needTaxation.as("isNeedTaxation"),
                        optionItem.discountPrice.as("discountPrice"),
                        optionItem.originPrice.as("originPrice"),
                        review.id.countDistinct().intValue().as("reviewCount"),
                        storeInfo.storeId.as("storeId"),
                        storeInfo.name.as("storeName"),
                        product.minOrderPrice.as("minOrderPrice"),
                        storeInfo.profileImage.as("storeImage"),
                        product.deliverFeeType.as("delieverFeeType"),
                        category.parentCategory.id.as("parentCategoryId")
                ))
                .from(product)
                .leftJoin(storeInfo).on(product.storeId.eq(storeInfo.storeId))
                .leftJoin(optionItem).on(product.representOptionItemId.eq(optionItem.id))
                .leftJoin(orderProductInfo).on(product.id.eq(orderProductInfo.productId)
                        .and(orderProductInfo.state.in(
                                OrderProductState.PAYMENT_DONE, OrderProductState.FINAL_CONFIRM)))
                .leftJoin(orders).on(orders.id.eq(orderProductInfo.orderId))
                .leftJoin(userInfo).on(userInfo.userId.eq(orders.userId)
                        .and(
                                userInfo.email.notLike("baroTastingNote")
                                        .and(userInfo.email.notLike("baroReviewId"))
                        )
                )
                .leftJoin(category).on(category.id.eq(product.category.id))
                .leftJoin(review).on(orderProductInfo.productId.eq(review.productId).and(review.isDeleted.eq(false)))
                .where(product.state.eq(ProductState.ACTIVE),
                        eqCuration(curationId),
                        isPromotionInProgress(),
                        eqStore(storeId),
                        isProductTitleLikeKeyword(keyword),
                        isIncludedCategory(categoryIds),
                        isIncludedSearchFilter(filterFieldsIds)
                )
                .groupBy(product.id)
                .orderBy(orderSpecifiers)
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetch();

        return new PageImpl<>(inquiryData, pageRequest, count);
    }

    private OrderSpecifier[] createPopularOrderSpecifier() {
        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();

        // 주문 많은 순
//        orderSpecifiers.add(new OrderSpecifier(
//                Order.DESC, orderProductInfo.productId.count()
//        ));

        // 리뷰 많은 순
        orderSpecifiers.add(new OrderSpecifier(
                Order.DESC, review.id.count()
        ));
        return orderSpecifiers.toArray(new OrderSpecifier[orderSpecifiers.size()]);
    }

    public int countPopularProducts(List<Integer> categoryIds, List<Integer> filterFieldsIds, Integer curationId, String keyword, Integer storeId) {
        int count = (int) queryFactory
                .select(product.count())
                .from(product)
                .where(product.state.eq(ProductState.ACTIVE),
                        eqCuration(curationId),
                        isPromotionInProgress(),
                        eqStore(storeId),
                        isProductTitleLikeKeyword(keyword),
                        isIncludedCategory(categoryIds),
                        isIncludedSearchFilter(filterFieldsIds)
                )
                .groupBy(product.id)
                .stream().count();
        return count;
    }

    public PageImpl<ProductListDto> selectDiscountProducts(PageRequest pageRequest, List<Integer> categoryIds,
                                                       List<Integer> filterFieldsIds, Integer curationId,
                                                       String keyword, Integer storeId) {
        int count = countDiscountProducts(categoryIds, filterFieldsIds, curationId, keyword, storeId);

        OrderSpecifier[] orderSpecifiers = createDiscountOrderSpecifier();

        List<ProductListDto> inquiryData = queryFactory
                .select(Projections.fields(
                        ProductListDto.class,
                        product.id.as("id"),
                        product.state.as("state"),
                        product.images.as("image"),
                        product.title.as("title"),
                        product.needTaxation.as("isNeedTaxation"),
                        optionItem.discountPrice.as("discountPrice"),
                        optionItem.originPrice.as("originPrice"),
                        review.id.countDistinct().intValue().as("reviewCount"),
                        storeInfo.storeId.as("storeId"),
                        storeInfo.name.as("storeName"),
                        product.minOrderPrice.as("minOrderPrice"),
                        storeInfo.profileImage.as("storeImage"),
                        product.deliverFeeType.as("delieverFeeType"),
                        category.parentCategory.id.as("parentCategoryId")
                ))
                .from(product)
                .leftJoin(storeInfo).on(product.storeId.eq(storeInfo.storeId))
                .leftJoin(optionItem).on(product.representOptionItemId.eq(optionItem.id))
                .leftJoin(orderProductInfo).on(product.id.eq(orderProductInfo.productId)
                        .and(orderProductInfo.state.in(
                                OrderProductState.PAYMENT_DONE, OrderProductState.FINAL_CONFIRM)))
                .leftJoin(orders).on(orders.id.eq(orderProductInfo.orderId))
                .leftJoin(userInfo).on(userInfo.userId.eq(orders.userId)
                        .and(
                                userInfo.email.notLike("baroTastingNote")
                                .and(userInfo.email.notLike("baroReviewId"))
                        )
                )
                .leftJoin(category).on(category.id.eq(product.category.id))
                .leftJoin(productSearchFilterMap).on(product.id.eq(productSearchFilterMap.productId))
                .leftJoin(searchFilterField).on(productSearchFilterMap.fieldId.eq(searchFilterField.id))
                .leftJoin(searchFilter).on(searchFilterField.searchFilterId.eq(searchFilter.id))
                .leftJoin(review).on(orderProductInfo.productId.eq(review.productId).and(review.isDeleted.eq(false)))
                .where(product.state.eq(ProductState.ACTIVE),
                        eqCuration(curationId),
                        isPromotionInProgress(),
                        eqStore(storeId),
                        isProductTitleLikeKeyword(keyword),
                        isIncludedCategory(categoryIds),
                        isIncludedSearchFilter(filterFieldsIds),
                        isDiscountApplied()
                )
                .groupBy(product.id)
                .orderBy(orderSpecifiers)
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetch();

        return new PageImpl<>(inquiryData, pageRequest, count);
    }

    public int countDiscountProducts(List<Integer> categoryIds, List<Integer> filterFieldsIds, Integer curationId, String keyword, Integer storeId) {
        int count =(int) queryFactory
                .select(product.count())
                .from(product)
                .leftJoin(productSearchFilterMap).on(product.id.eq(productSearchFilterMap.productId))
                .leftJoin(searchFilterField).on(productSearchFilterMap.fieldId.eq(searchFilterField.id))
                .leftJoin(searchFilter).on(searchFilterField.searchFilterId.eq(searchFilter.id))
                .leftJoin(review).on(userInfo.userId.eq(review.userId).and(review.isDeleted.eq(false)))
                .where(product.state.eq(ProductState.ACTIVE),
                        eqCuration(curationId),
                        isPromotionInProgress(),
                        eqStore(storeId),
                        isProductTitleLikeKeyword(keyword),
                        isIncludedCategory(categoryIds),
                        isIncludedSearchFilter(filterFieldsIds),
                        isDiscountApplied()
                )
                .groupBy(product.id)
                .stream().count();
        return count;
    }

    private OrderSpecifier[] createDiscountOrderSpecifier() {
        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();

        orderSpecifiers.add(new OrderSpecifier(
                Order.DESC, optionItem.discountPrice.divide(optionItem.originPrice)
                )
        );
        return orderSpecifiers.toArray(new OrderSpecifier[orderSpecifiers.size()]);
    }

    private BooleanExpression excludeIntendedReviews() {
        return userInfo.email.notLike("baroTastingNote")
                .and(userInfo.email.notLike("baroReviewId"));
    }

    private BooleanExpression isDiscountApplied() {
        return optionItem.originPrice.notIn(0);
    }

    private BooleanExpression isIncludedSearchFilter(List<Integer> filterFieldsIds) {
        if (filterFieldsIds == null || filterFieldsIds.isEmpty()) {
            return null;
        }

        // searchFilterId - searchFilterFields로 매핑되는 해시맵을 만들어요
        Map<Integer, List<Integer>> filterAndFieldMapper = new HashMap<>();
        List<SearchFilterField> searchFilterFields = searchFilterFieldRepository.findAllById(filterFieldsIds);

        for (SearchFilterField filterField : searchFilterFields) {
            int searchFilterId = filterField.getSearchFilterId();
            List<Integer> existingValue = filterAndFieldMapper.getOrDefault(searchFilterId, new ArrayList<>());
            existingValue.add(filterField.getId());
            filterAndFieldMapper.put(
                    searchFilterId,
                    existingValue
            );
        }

        // 위에서 만들어진 해시맵으로 필터1(필드) && 필터2(필드) .. 조건을 만들어요
        BooleanExpression booleanExpression = null;
        for (Integer filterId : filterAndFieldMapper.keySet()) {

            BooleanExpression filterCondition = product.id.in(
                    JPAExpressions
                            .select(productSearchFilterMap.productId)
                            .from(productSearchFilterMap)
                            .where(productSearchFilterMap.fieldId.in(filterAndFieldMapper.get(filterId)))
            );

            if (booleanExpression == null) {
                booleanExpression = filterCondition;
            } else {
                booleanExpression = booleanExpression.and(filterCondition);
            }
        }
        return booleanExpression;
    }

    private BooleanExpression isIncludedCategory(List<Integer> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return null;
        }
        return category.parentCategory.id.in(categoryIds)
                .or(category.id.in(categoryIds));
    }

    private BooleanExpression isProductTitleLikeKeyword(String keyword) {
        if (StringUtils.isEmpty(keyword)) {
            return null;
        }
        return product.title.contains(keyword);
    }

    private BooleanExpression eqCuration(Integer curationId) {
        if (curationId == null) {
            return null;
        }
        return curationProductMap.curation.id.eq(curationId);
    }

    private BooleanExpression eqStore(Integer storeId) {
        if (storeId == null) {
            return null;
        }
        return storeInfo.storeId.eq(storeId);
    }

    private BooleanBuilder isPromotionInProgress() {
        BooleanBuilder builder = new BooleanBuilder();
        builder
                // or 조건을 만들기 위해 사용
                .andAnyOf(
                        // product.promotionStartAt이 null이거나 현재 시간보다 작은 product를 선택
                        product.promotionStartAt.isNull(),
                        product.promotionStartAt.lt(Timestamp.valueOf(LocalDateTime.now()))
                )
                .andAnyOf(
                        product.promotionEndAt.isNull(),
                        product.promotionEndAt.gt(Timestamp.valueOf(LocalDateTime.now()))
                );
        return builder;
    }
}
