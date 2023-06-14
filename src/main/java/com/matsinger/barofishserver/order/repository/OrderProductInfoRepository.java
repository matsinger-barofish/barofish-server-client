package com.matsinger.barofishserver.order.repository;

import com.matsinger.barofishserver.order.object.OrderProductInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderProductInfoRepository extends JpaRepository<OrderProductInfo, Integer> {
    List<OrderProductInfo> findAllByOrderId(String orderId);
}
