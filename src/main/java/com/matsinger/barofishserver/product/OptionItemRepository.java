package com.matsinger.barofishserver.product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OptionItemRepository extends JpaRepository<OptionItem, Integer> {
    Optional<OptionItem> findByName(String name);
}
