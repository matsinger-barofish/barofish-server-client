package com.matsinger.barofishserver.product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface ProductRepository extends JpaRepository<Product, Integer> {
    public List<Product> findByTitleContainsAndStateEquals(String title,ProductState state);

    Optional<Product> findByTitle(String title);
}
