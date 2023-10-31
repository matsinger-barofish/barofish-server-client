package com.matsinger.barofishserver.domain.order.repository;

import com.matsinger.barofishserver.domain.order.domain.OrderDeliverPlace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDeliverPlaceRepository extends JpaRepository<OrderDeliverPlace, String> {
    void deleteAllByOrderIdIn(List<String> orderIds);
}
