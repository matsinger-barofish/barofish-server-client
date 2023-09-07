package com.matsinger.barofishserver.order.orderprductinfo.repository;

import com.matsinger.barofishserver.order.orderprductinfo.domain.OrderProductInfo;
import com.matsinger.barofishserver.order.orderprductinfo.domain.OrderProductState;
import com.matsinger.barofishserver.settlement.dto.SettlementExcelDownloadRawDto;
import com.matsinger.barofishserver.settlement.dto.SettlementOrderDto;
import com.matsinger.barofishserver.settlement.dto.SettlementProductOptionItemDto;
import com.matsinger.barofishserver.settlement.dto.SettlementStoreDto;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.matsinger.barofishserver.coupon.domain.QCoupon.coupon;
import static com.matsinger.barofishserver.order.domain.QOrderDeliverPlace.orderDeliverPlace;
import static com.matsinger.barofishserver.order.domain.QOrders.orders;
import static com.matsinger.barofishserver.order.orderprductinfo.domain.QOrderProductInfo.orderProductInfo;
import static com.matsinger.barofishserver.product.domain.QProduct.product;
import static com.matsinger.barofishserver.product.optionitem.domain.QOptionItem.optionItem;
import static com.matsinger.barofishserver.store.domain.QStore.store;
import static com.matsinger.barofishserver.store.domain.QStoreInfo.storeInfo;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;

@Repository
@RequiredArgsConstructor
public class OrderProductInfoRepositoryImpl implements OrderProductInfoRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public OrderProductInfo findByIdQ(int orderProductInfoId) {
        return queryFactory.selectFrom(orderProductInfo)
                .where(orderProductInfo.id.eq(orderProductInfoId))
                .fetchOne();
    }

    @Override
    public List<SettlementExcelDownloadRawDto> getExcelRawDataWithNotSettled1() {
        return queryFactory
                .select(Projections.fields(SettlementExcelDownloadRawDto.class,
                        product.id.as("productId"),
                        orders.id.as("orderId"),
                        orderProductInfo.state.as("orderProductInfoState"),
                        orders.orderedAt.as("orderedAt"),
                        orderProductInfo.finalConfirmedAt.as("finalConfirmedAt"),
                        storeInfo.name.as("partnerName"),
                        product.title.as("productName"),
                        optionItem.name.as("optionItemName"),
                        product.needTaxation.as("isTaxFree"),
                        optionItem.purchasePrice.as("purchasePrice"),
                        Expressions.as(Expressions.constant(0), "commissionPrice"), // 수수료가
                        ExpressionUtils.as(Expressions.constant(0), "sellingPrice"), // 판매가
                        orderProductInfo.deliveryFee.as("deliveryFee"),
                        orderProductInfo.amount.as("quantity"),
                        ExpressionUtils.as(Expressions.constant(0), "totalPrice"), // 총 금액
                        ExpressionUtils.as(Expressions.constant(0), "totalOrderPrice"), // 총 주문금액
                        coupon.title.as("couponName"),
                        coupon.amount.as("couponDiscount"),
                        orders.usePoint.as("usePoint"),
                        ExpressionUtils.as(Expressions.constant(0), "finalPaymentPrice"), // 최종 결제금액
                        orders.paymentWay.as("paymentWay"),
                        storeInfo.settlementRate.as("settlementRate"),
                        ExpressionUtils.as(Expressions.constant(0), "settlementPrice"), // 정산금액
                        orderProductInfo.isSettled.as("settlementState"),
                        orderProductInfo.settledAt.as("settledAt"),
                        orderDeliverPlace.receiverName.as("customerName"),
                        orderDeliverPlace.tel.as("phoneNumber"),
                        ExpressionUtils.as(Expressions.constant(""), "email"), // 이메일
                        ExpressionUtils.as(Expressions.constant(""), "address"), // 주소
                        orderDeliverPlace.deliverMessage.as("deliverMessage"),
                        ExpressionUtils.as(Expressions.constant(""), "deliveryCompany"), // 택배사
                        orderProductInfo.invoiceCode.as("invoiceCode"))
                )
                .from(orderProductInfo)
                .leftJoin(optionItem).on(orderProductInfo.optionItemId.eq(optionItem.id))
                .leftJoin(product).on(product.id.eq(orderProductInfo.productId)).fetchJoin()
                .leftJoin(orders).on(orders.id.eq(orderProductInfo.orderId)).fetchJoin()
                .leftJoin(orderDeliverPlace).on(orders.id.eq(orderDeliverPlace.orderId))
                .leftJoin(coupon).on(coupon.id.eq(orders.couponId)).fetchJoin()
                .leftJoin(store).on(store.id.eq(product.storeId)).fetchJoin()
                .leftJoin(storeInfo).on(storeInfo.storeId.eq(store.id)).fetchJoin()
                .where(orderProductInfo.state.eq(OrderProductState.FINAL_CONFIRM))
                .orderBy(orderProductInfo.orderId.desc())
                .orderBy(product.storeId.asc())
                .fetch();
    }

//    @Override
//    public List<SettlementOrderDto> getExcelRawDataWithNotSettled2() {
//
//        return queryFactory
//                .selectFrom(orderProductInfo)
//                .leftJoin(optionItem).on(orderProductInfo.optionItemId.eq(optionItem.id))
//                .leftJoin(product).on(product.id.eq(orderProductInfo.productId)).fetchJoin()
//                .leftJoin(orders).on(orders.id.eq(orderProductInfo.orderId)).fetchJoin()
//                .leftJoin(orderDeliverPlace).on(orders.id.eq(orderDeliverPlace.orderId))
//                .leftJoin(coupon).on(coupon.id.eq(orders.couponId)).fetchJoin()
//                .leftJoin(store).on(store.id.eq(product.storeId)).fetchJoin()
//                .leftJoin(storeInfo).on(storeInfo.storeId.eq(store.id)).fetchJoin()
//                .where(orderProductInfo.state.eq(OrderProductState.FINAL_CONFIRM))
//                .orderBy(orderProductInfo.orderId.desc())
//                .orderBy(product.storeId.asc())
//                .transform(
//                        groupBy(orders.id, store.id)
//                                .list(Projections.fields(SettlementOrderDto.class,
//                                        orders.id.as("orderId"),
//                                        list(Projections.fields(SettlementStoreDto.class,
//                                                storeInfo.name.as("partnerName"),
//                                                list(Projections.fields(SettlementProductOptionItemDto.class,
//                                                        product.id.as("productId"))
//                                                ))
//                                        ))
//                                )
//                );
//    }
@Override
public List<SettlementOrderDto> getExcelRawDataWithNotSettled2() {

    return queryFactory
            .selectFrom(orderProductInfo)
            .leftJoin(optionItem).on(orderProductInfo.optionItemId.eq(optionItem.id))
            .leftJoin(product).on(product.id.eq(orderProductInfo.productId))
            .leftJoin(orders).on(orders.id.eq(orderProductInfo.orderId))
            .leftJoin(orderDeliverPlace).on(orders.id.eq(orderDeliverPlace.orderId))
            .leftJoin(coupon).on(coupon.id.eq(orders.couponId))
            .leftJoin(store).on(store.id.eq(product.storeId))
            .leftJoin(storeInfo).on(storeInfo.storeId.eq(store.id))
            .where(orderProductInfo.state.eq(OrderProductState.FINAL_CONFIRM))
            .orderBy(orderProductInfo.orderId.desc())
            .orderBy(product.storeId.asc())
            .transform(
                    groupBy(orders.id, store.id)
                            .list(Projections.fields(SettlementOrderDto.class,
                                    orders.id.as("orderId"),
                                    list(Projections.fields(SettlementStoreDto.class,
                                            store.id.as("storeId"),
                                            storeInfo.name.as("partnerName"),
                                            list(Projections.fields(SettlementProductOptionItemDto.class,
                                                    product.title.as("productName"),
                                                    optionItem.name.as("optionItemName"),
                                                    product.id.as("productId")
                                            )).as("productOptionItemDtos")
                                    )).as("settlementStoreDtos")
                            ))
            );
    }
}
