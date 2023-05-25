package com.matsinger.barofishserver.order.repository;

import com.matsinger.barofishserver.order.Order;
import com.matsinger.barofishserver.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, String>, OrderRepositoryCustom {
    Optional<List<Order>> findByUser(User user);
}
