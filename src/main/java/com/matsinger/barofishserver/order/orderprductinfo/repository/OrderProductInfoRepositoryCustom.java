package com.matsinger.barofishserver.order.orderprductinfo.repository;

import com.matsinger.barofishserver.order.orderprductinfo.domain.OrderProductInfo;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderProductInfoRepositoryCustom {

    OrderProductInfo findByIdQ(int orderProductInfoId);
}
