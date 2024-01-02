package com.matsinger.barofishserver.domain.basketProduct.repository;


import com.matsinger.barofishserver.domain.basketProduct.dto.BasketStoreInquiryDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BasketQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<BasketStoreInquiryDto> selectBasketProducts(int userId) {
//        return queryFactory
//                .selectFrom(basketProductInfo)
//                .leftJoin(userInfo).on(basketProductInfo.userId.eq(userInfo.userId))
//                .leftJoin(optionItem).on(optionItem.id.eq(basketProductInfo.optionItemId))
//                .leftJoin(option).on(option.id.eq(basketProductInfo.optionId))
//                .leftJoin(product).on(product.id.eq(basketProductInfo.productId))
//                .leftJoin(storeInfo).on(storeInfo.storeId.eq(basketProductInfo.storeId))
//                .where(basketProductInfo.userId.eq(userId))
//                .transform(groupBy(basketProductInfo.storeId)
//                        .list(Projections.fields(
//                                BasketStoreInquiryDto.class,
//                                userInfo.userId.as("userId"),
//                                storeInfo.storeId.as("storeId"),
//                                storeInfo.name.as("storeName"),
//                                storeInfo.profileImage.as("profileImage"),
//                                GroupBy.list(Projections.fields(
//                                        BasketProductInquiryDto.class,
//                                        product.id.as("productId"),
//                                        product.title.as("productName"),
//                                        optionItem.id.as("optionItemId"),
//                                        optionItem.name.as("optionItemName"),
//                                        option.isNeeded.as("isNeeded"),
//                                        optionItem.discountPrice.as("price"),
//                                        basketProductInfo.amount.as("quantity"),
//                                        product.deliverFeeType.as("deliveryFeeType"),
//                                        product.deliverFee.as("deliveryFee")
//                                )).as("products")
//                        ))
//                );
        return null;
    }
}
