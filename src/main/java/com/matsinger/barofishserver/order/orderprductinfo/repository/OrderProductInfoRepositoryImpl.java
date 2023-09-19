package com.matsinger.barofishserver.order.orderprductinfo.repository;

import com.matsinger.barofishserver.order.orderprductinfo.domain.OrderProductState;
import com.matsinger.barofishserver.review.domain.Review;
import com.matsinger.barofishserver.settlement.dto.SettlementOrderRawDto;
import com.matsinger.barofishserver.settlement.dto.SettlementProductOptionItemDto;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.matsinger.barofishserver.coupon.domain.QCoupon.coupon;
import static com.matsinger.barofishserver.deliver.domain.QDeliveryCompany.deliveryCompany;
import static com.matsinger.barofishserver.order.domain.QOrderDeliverPlace.orderDeliverPlace;
import static com.matsinger.barofishserver.order.domain.QOrders.orders;
import static com.matsinger.barofishserver.order.orderprductinfo.domain.QOrderProductInfo.orderProductInfo;
import static com.matsinger.barofishserver.product.domain.QProduct.product;
import static com.matsinger.barofishserver.product.optionitem.domain.QOptionItem.optionItem;
import static com.matsinger.barofishserver.review.domain.QReview.review;
import static com.matsinger.barofishserver.review.domain.QReviewLike.reviewLike;
import static com.matsinger.barofishserver.store.domain.QStore.store;
import static com.matsinger.barofishserver.store.domain.QStoreInfo.storeInfo;
import static com.matsinger.barofishserver.user.domain.QUser.user;
import static com.matsinger.barofishserver.userinfo.domain.QUserInfo.userInfo;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;

@Repository
@RequiredArgsConstructor
public class OrderProductInfoRepositoryImpl implements OrderProductInfoRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Review> queryTest(int orderProductInfoId) {
        return queryFactory
                .select(review)
                .from(review)
                .leftJoin(reviewLike).on(review.id.eq(reviewLike.reviewId))
                .groupBy(review.id)
                .fetch();

    }

    @Override
    public List<SettlementOrderRawDto> getExcelRawDataWithNotSettled(Integer storeId) {

        return queryFactory
                .selectFrom(orderProductInfo)
                .leftJoin(optionItem).on(orderProductInfo.optionItemId.eq(optionItem.id))
                .leftJoin(product).on(product.id.eq(orderProductInfo.productId))
                .leftJoin(orders).on(orders.id.eq(orderProductInfo.orderId))
                .leftJoin(orderDeliverPlace).on(orders.id.eq(orderDeliverPlace.orderId))
                .leftJoin(coupon).on(coupon.id.eq(orders.couponId))
                .leftJoin(store).on(store.id.eq(product.storeId))
                .leftJoin(storeInfo).on(storeInfo.storeId.eq(store.id))
                .leftJoin(user).on(orders.userId.eq(user.id))
                .leftJoin(userInfo).on(userInfo.userId.eq(user.id))
                .leftJoin(deliveryCompany).on(orderProductInfo.deliverCompanyCode.eq(deliveryCompany.code))
                .where(orderProductInfo.state.eq(OrderProductState.FINAL_CONFIRM), eqStoreId(storeId))
                .orderBy(orders.id.desc())
                .orderBy(store.id.desc())
                .transform(
                        groupBy(orders.id, store.id)
                                .list(Projections.fields(
                                        SettlementOrderRawDto.class,
                                        orders.id.as("orderId"),
                                        ExpressionUtils.as(Expressions.constant(0), "finalOrderPrice"),
                                        coupon.title.as("couponName"),
                                        coupon.amount.as("couponDiscount"),
                                        orders.usePoint.as("usePoint"),
                                        ExpressionUtils.as(Expressions.constant(0), "orderDeliveryFeeSum"),
                                        // 위는 order 끝나면 출력, 밑은 store 끝나면 출력
                                        store.id.as("storeId"),
                                        storeInfo.name.as("partnerName"),
                                        storeInfo.settlementRate.as("settlementRate"),
                                        ExpressionUtils.as(Expressions.constant(0), "storeDeliveryFeeSum"),
                                        ExpressionUtils.as(Expressions.constant(0), "storeTotalPriceSum"),

                                        list(Projections.fields(
                                                SettlementProductOptionItemDto.class,
                                                product.id.as("productId"),
                                                product.title.as("productName"),
                                                optionItem.name.as("optionItemName"),
                                                orderProductInfo.state.as("orderProductInfoState"),
                                                orders.orderedAt.as("orderedAt"),
                                                orderProductInfo.finalConfirmedAt.as("finalConfirmedAt"),
                                                product.needTaxation.as("isTaxFree"),
                                                optionItem.purchasePrice.as("purchasePrice"),
                                                ExpressionUtils.as(Expressions.constant(0), "commissionPrice"),
                                                orderProductInfo.originPrice.as("sellingPrice"),
                                                orderProductInfo.deliveryFee.as("deliveryFee"),
                                                orderProductInfo.amount.as("quantity"),
                                                ExpressionUtils.as(Expressions.constant(0), "totalPrice"),
                                                orders.paymentWay.as("paymentWay"),
                                                ExpressionUtils.as(Expressions.constant(0), "settlementPrice"),
                                                orderProductInfo.isSettled.as("settlementState"),
                                                orderProductInfo.settledAt.as("settledAt"),
                                                orderDeliverPlace.name.as("customerName"),
                                                orderDeliverPlace.tel.as("phoneNumber"),
                                                userInfo.email.as("email"),
                                                orderDeliverPlace.address.as("address"),
                                                orderDeliverPlace.addressDetail.as("addressDetail"),
                                                orderDeliverPlace.deliverMessage.as("deliverMessage"),
                                                deliveryCompany.name.as("deliveryCompany"),
                                                orderProductInfo.invoiceCode.as("invoiceCode")
                                        )).as("settlementProductOptionItemDtos")
                                ))
                );
    }

    private BooleanExpression eqStoreId(Integer storeId) {
        if (storeId == null) {
            return null;
        }
        return store.id.eq(storeId);
    }
}
