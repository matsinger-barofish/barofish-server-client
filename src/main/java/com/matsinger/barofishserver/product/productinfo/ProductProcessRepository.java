package com.matsinger.barofishserver.product.productinfo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductProcessRepository extends JpaRepository<ProductProcess, Integer> {
}