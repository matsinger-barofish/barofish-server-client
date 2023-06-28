package com.matsinger.barofishserver.order.repository;

import com.matsinger.barofishserver.order.object.OrderProductInfo;
import com.matsinger.barofishserver.order.object.OrderProductState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface OrderProductInfoRepository extends JpaRepository<OrderProductInfo, Integer>,
        JpaSpecificationExecutor<OrderProductInfo> {
    List<OrderProductInfo> findAllByOrderId(String orderId);

    List<OrderProductInfo> findAllByStateIn(List<OrderProductState> state);

    List<OrderProductInfo> findAllByIdIn(List<Integer> ids);

    List<OrderProductInfo> findAllByProduct_StoreIdAndIsSettled(Integer storeId, Boolean isSettled);
}
