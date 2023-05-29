package com.matsinger.barofishserver.order.repository;

import com.matsinger.barofishserver.order.OrderStoreInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderStoreInfoRepository extends JpaRepository<OrderStoreInfo, Integer> {
}
