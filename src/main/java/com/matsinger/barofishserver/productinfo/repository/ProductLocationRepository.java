package com.matsinger.barofishserver.productinfo.repository;

import com.matsinger.barofishserver.productinfo.domain.ProductLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductLocationRepository extends JpaRepository<ProductLocation, Integer> {
}
