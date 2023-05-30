package com.matsinger.barofishserver.order.repository;

import com.matsinger.barofishserver.order.Order;

import java.util.Optional;

public interface OrderRepositoryCustom {

    Optional<Order> createSequence(Order order);
}
