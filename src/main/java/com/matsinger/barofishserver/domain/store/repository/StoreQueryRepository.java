package com.matsinger.barofishserver.domain.store.repository;

import com.matsinger.barofishserver.domain.order.orderprductinfo.domain.OrderProductState;
import com.matsinger.barofishserver.domain.store.domain.StoreState;
import com.matsinger.barofishserver.domain.store.dto.SimpleStore;
import com.matsinger.barofishserver.domain.store.dto.StoreExcelInquiryDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.matsinger.barofishserver.domain.order.domain.QOrders.orders;
import static com.matsinger.barofishserver.domain.order.orderprductinfo.domain.QOrderProductInfo.orderProductInfo;
import static com.matsinger.barofishserver.domain.product.domain.QProduct.product;
import static com.matsinger.barofishserver.domain.review.domain.QReview.review;
import static com.matsinger.barofishserver.domain.store.domain.QStore.store;
import static com.matsinger.barofishserver.domain.store.domain.QStoreInfo.storeInfo;
import static com.matsinger.barofishserver.domain.store.domain.QStoreScrap.storeScrap;

@Repository
@RequiredArgsConstructor
public class StoreQueryRepository {

    private final JPAQueryFactory queryFactory;


    public List<StoreExcelInquiryDto> getExcelDataByStoreIds(List<Integer> storeIds) {
        return queryFactory.select(Projections.fields(
                StoreExcelInquiryDto.class,
                storeInfo.name.as("storeName"),
                store.loginId.as("loginId"),
                store.state.as("state"),
                storeInfo.location.as("location"),
                storeInfo.keyword.as("keyowrd"),
                storeInfo.settlementRate.as("settlementRete"),
                storeInfo.bankName.as("bankName"),
                storeInfo.bankHolder.as("bankHolder"),
                storeInfo.bankAccount.as("bankAccount"),
                storeInfo.representativeName.as("representativeName"),
                storeInfo.companyId.as("companyId"),
                storeInfo.businessType.as("businessType"),
                storeInfo.mosRegistrationNumber.as("mosRegistrationNumber"),
                storeInfo.businessAddress.as("businessAddress"),
                storeInfo.postalCode.as("postalCode"),
                storeInfo.lotNumberAddress.as("lotNumberAddress"),
                storeInfo.streetNameAddress.as("streetNameAddress"),
                storeInfo.addressDetail.as("addressDetail"),
                storeInfo.tel.as("phoneNumber"),
                storeInfo.email.as("email"),
                storeInfo.faxNumber.as("faxNumber")
                ))
                .from(store)
                .leftJoin(storeInfo).on(store.id.eq(storeInfo.storeId))
                .where(
                        containsStoreIds(storeIds)
                )
                .orderBy(store.id.asc())
                .fetch();
    }

    private BooleanExpression containsStoreIds(List<Integer> storeIds) {
        if (storeIds == null || storeIds.isEmpty()) {
            return null;
        }
        return store.id.in(storeIds);
    }

    public List<SimpleStore> selectRecommendStoreWithJoinAt(PageRequest pageRequest,
                                               String keyword,
                                               Integer userId) {
        return queryFactory.select(Projections.fields(
                        SimpleStore.class,
                        store.id.as("storeId"),
                        store.loginId.as("loginId"),
                        storeInfo.backgroudImage.as("backgroundImage"),
                        storeInfo.isReliable.as("isReliable"),
                        storeInfo.profileImage.as("profileImage"),
                        storeInfo.name.as("name"),
                        storeInfo.location.as("location"),
                        storeInfo.keyword.as("keyword"),
                        storeInfo.visitNote.as("visitNote"),
                        storeInfo.refundDeliverFee.as("refundDeliveryFee"),
                        storeInfo.oneLineDescription.as("oneLineDescription"),
                        storeInfo.deliverCompany.as("deliverCompany"),
                        storeScrap.userId.eq(userId).isTrue().as("isLike")
                ))
                .from(storeInfo)
                .join(store).on(store.id.eq(storeInfo.storeId))
                .leftJoin(storeScrap).on(store.id.eq(storeScrap.storeId))
                .where(store.state.eq(StoreState.ACTIVE)
                        .and(storeInfo.name.contains(keyword))
                )
                .orderBy(store.joinAt.desc())
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetch();
    }

    public List<SimpleStore> selectRecommendStoreWithScrape(PageRequest pageRequest,
                                               String keyword,
                                               Integer userId) {
        return queryFactory.select(Projections.fields(
                        SimpleStore.class,
                        store.id.as("storeId"),
                        store.loginId.as("loginId"),
                        storeInfo.backgroudImage.as("backgroundImage"),
                        storeInfo.isReliable.as("isReliable"),
                        storeInfo.profileImage.as("profileImage"),
                        storeInfo.name.as("name"),
                        storeInfo.location.as("location"),
                        storeInfo.keyword.as("keyword"),
                        storeInfo.visitNote.as("visitNote"),
                        storeInfo.refundDeliverFee.as("refundDeliveryFee"),
                        storeInfo.oneLineDescription.as("oneLineDescription"),
                        storeInfo.deliverCompany.as("deliverCompany"),
                        storeScrap.userId.eq(userId).isTrue().as("isLike")
                ))
                .from(storeInfo)
                .join(store).on(store.id.eq(storeInfo.storeId))
                .leftJoin(storeScrap).on(store.id.eq(storeScrap.storeId))
                .where(store.state.eq(StoreState.ACTIVE)
                        .and(storeInfo.name.contains(keyword))
                )
                .orderBy(storeInfo.storeId.count().desc())
                .groupBy(storeInfo.storeId)
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetch();
    }

    public List<SimpleStore> selectRecommendStoreWithReview(PageRequest pageRequest,
                                               String keyword,
                                               Integer userId) {
        return queryFactory.select(Projections.fields(
                        SimpleStore.class,
                        store.id.as("storeId"),
                        store.loginId.as("loginId"),
                        storeInfo.backgroudImage.as("backgroundImage"),
                        storeInfo.isReliable.as("isReliable"),
                        storeInfo.profileImage.as("profileImage"),
                        storeInfo.name.as("name"),
                        storeInfo.location.as("location"),
                        storeInfo.keyword.as("keyword"),
                        storeInfo.visitNote.as("visitNote"),
                        storeInfo.refundDeliverFee.as("refundDeliveryFee"),
                        storeInfo.oneLineDescription.as("oneLineDescription"),
                        storeInfo.deliverCompany.as("deliverCompany"),
                        storeScrap.userId.eq(userId).isTrue().as("isLike")
                ))
                .from(storeInfo)
                .leftJoin(review).on(storeInfo.storeId.eq(review.storeId).and(review.isDeleted.eq(false)))
                .leftJoin(storeScrap).on(store.id.eq(storeScrap.storeId))
                .where(store.state.eq(StoreState.ACTIVE)
                        .and(storeInfo.name.contains(keyword))
                )
                .groupBy(storeInfo.storeId)
                .orderBy(review.storeId.count().desc())
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetch();
    }

    public List<SimpleStore> selectRecommendStoreWithOrder(PageRequest pageRequest,
                                              String keyword,
                                              Integer userId) {
        return queryFactory.select(Projections.fields(
                        SimpleStore.class,
                        store.id.as("storeId"),
                        store.loginId.as("loginId"),
                        storeInfo.backgroudImage.as("backgroundImage"),
                        storeInfo.isReliable.as("isReliable"),
                        storeInfo.profileImage.as("profileImage"),
                        storeInfo.name.as("name"),
                        storeInfo.location.as("location"),
                        storeInfo.keyword.as("keyword"),
                        storeInfo.visitNote.as("visitNote"),
                        storeInfo.refundDeliverFee.as("refundDeliveryFee"),
                        storeInfo.oneLineDescription.as("oneLineDescription"),
                        storeInfo.deliverCompany.as("deliverCompany"),
                        storeScrap.userId.eq(userId).isTrue().as("isLike")
                ))
                .from(storeInfo)
                .join(product).on(store.id.eq(product.storeId))
                .leftJoin(orderProductInfo).on(orderProductInfo.productId.eq(product.id))
                .leftJoin(orders).on(orders.id.eq(orderProductInfo.orderId))
                .leftJoin(storeScrap).on(storeInfo.storeId.eq(storeScrap.storeId))
                .where(store.state.eq(StoreState.ACTIVE)
                        .and(storeInfo.name.contains(keyword))
                )
                .groupBy(storeInfo.storeId)
                .orderBy(
                        orderProductInfo.state.eq(OrderProductState.FINAL_CONFIRM).count().desc()
                )
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetch();
    }

    public List<SimpleStore> selectReliableStoreRandomOrder(Integer userId) {
        return queryFactory.select(Projections.fields(
                        SimpleStore.class,
                        store.id.as("storeId"),
                        store.loginId.as("loginId"),
                        storeInfo.backgroudImage.as("backgroundImage"),
                        storeInfo.isReliable.as("isReliable"),
                        storeInfo.profileImage.as("profileImage"),
                        storeInfo.name.as("name"),
                        storeInfo.location.as("location"),
                        storeInfo.keyword.as("keyword"),
                        storeInfo.visitNote.as("visitNote"),
                        storeInfo.refundDeliverFee.as("refundDeliveryFee"),
                        storeInfo.oneLineDescription.as("oneLineDescription"),
                        storeInfo.deliverCompany.as("deliverCompany"),
                        storeScrap.userId.eq(userId).isTrue().as("isLike")
                ))
                .from(storeInfo)
                .leftJoin(storeScrap).on(storeInfo.storeId.eq(storeScrap.storeId))
                .where(storeInfo.isReliable)
                .orderBy(NumberExpression.random().asc())
                .fetch();
    }
}
