package com.matsinger.barofishserver.domain.basketProduct.repository;


import com.matsinger.barofishserver.domain.basketProduct.dto.BasketProductInquiryDto;
import com.matsinger.barofishserver.domain.basketProduct.dto.BasketStoreInquiryDto;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.matsinger.barofishserver.domain.basketProduct.domain.QBasketProductInfo.basketProductInfo;
import static com.matsinger.barofishserver.domain.product.domain.QProduct.product;
import static com.matsinger.barofishserver.domain.product.option.domain.QOption.option;
import static com.matsinger.barofishserver.domain.product.optionitem.domain.QOptionItem.optionItem;
import static com.matsinger.barofishserver.domain.store.domain.QStoreInfo.storeInfo;
import static com.matsinger.barofishserver.domain.userinfo.domain.QUserInfo.userInfo;
import static com.querydsl.core.group.GroupBy.groupBy;

@Repository
@RequiredArgsConstructor
public class BasketQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<BasketStoreInquiryDto> selectBasketProducts(int userId) {
        return queryFactory
                .selectFrom(basketProductInfo)
                .leftJoin(userInfo).on(basketProductInfo.userId.eq(userInfo.userId))
                .leftJoin(optionItem).on(optionItem.id.eq(basketProductInfo.optionItemId))
                .leftJoin(option).on(option.id.eq(basketProductInfo.optionId))
                .leftJoin(product).on(product.id.eq(basketProductInfo.productId))
                .leftJoin(storeInfo).on(storeInfo.storeId.eq(basketProductInfo.storeId))
                .where(basketProductInfo.userId.eq(userId))
                .transform(groupBy(basketProductInfo.storeId)
                        .list(Projections.fields(
                                BasketStoreInquiryDto.class,
                                userInfo.userId.as("userId"),
                                storeInfo.storeId.as("storeId"),
                                storeInfo.name.as("storeName"),
                                storeInfo.profileImage.as("profileImage"),
                                GroupBy.list(Projections.fields(
                                        BasketProductInquiryDto.class,
                                        product.id.as("productId"),
                                        product.title.as("productName"),
                                        optionItem.id.as("optionItemId"),
                                        optionItem.name.as("optionItemName"),
                                        option.isNeeded.as("isNeeded"),
                                        optionItem.discountPrice.as("price"),
                                        basketProductInfo.amount.as("quantity"),
                                        product.deliverFeeType.as("deliveryFeeType"),
                                        product.deliverFee.as("deliveryFee")
                                )).as("products")
                        ))
                );
    }
}
