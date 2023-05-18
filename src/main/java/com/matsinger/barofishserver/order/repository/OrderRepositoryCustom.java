package com.matsinger.barofishserver.order.repository;

import com.matsinger.barofishserver.order.Order;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OrderRepositoryCustom {

    Optional<Order> createSequenceAndSave(Order order);

    int calculateCurrentSequence(LocalDateTime orderDateTime);

    String generateOrderNumber(LocalDateTime orderDateTime);
}
