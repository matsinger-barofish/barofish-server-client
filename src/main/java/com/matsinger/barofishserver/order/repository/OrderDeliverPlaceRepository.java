package com.matsinger.barofishserver.order.repository;

import com.matsinger.barofishserver.order.object.OrderDeliverPlace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDeliverPlaceRepository extends JpaRepository<OrderDeliverPlace, String > {
}
