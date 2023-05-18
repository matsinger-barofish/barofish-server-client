package com.matsinger.barofishserver.order.repository;

import com.matsinger.barofishserver.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, String>, OrderRepositoryCustom {
}
