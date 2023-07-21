package com.matsinger.barofishserver.productinfo.repository;

import com.matsinger.barofishserver.productinfo.domain.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductTypeRepository extends JpaRepository<ProductType, Integer> {
}
