package com.matsinger.barofishserver.order.repository;

import com.matsinger.barofishserver.order.OrderProductInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProductInfoRepository extends JpaRepository<OrderProductInfo, Long> {
}
