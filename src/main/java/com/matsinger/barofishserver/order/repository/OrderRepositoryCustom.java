package com.example.demo.domain.repository;

import com.example.demo.domain.entity.Order;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OrderRepositoryCustom {

    Optional<Order> createSequenceAndSave(Order order);

    int calculateCurrentSequence(LocalDateTime orderDateTime);

    String generateOrderNumber(LocalDateTime orderDateTime);
}
