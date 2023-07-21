package com.matsinger.barofishserver.productinfo.repository;

import com.matsinger.barofishserver.productinfo.domain.ProductUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductUsageRepository extends JpaRepository<ProductUsage, Integer> {
}
