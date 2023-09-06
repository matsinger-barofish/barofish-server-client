package com.matsinger.barofishserver.order.orderprductinfo.repository;

import com.matsinger.barofishserver.order.orderprductinfo.domain.OrderProductInfo;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.matsinger.barofishserver.order.orderprductinfo.domain.QOrderProductInfo.orderProductInfo;

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
}
