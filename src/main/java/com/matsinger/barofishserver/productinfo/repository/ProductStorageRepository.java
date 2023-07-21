package com.matsinger.barofishserver.productinfo.repository;

import com.matsinger.barofishserver.productinfo.domain.ProductStorage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductStorageRepository extends JpaRepository<ProductStorage, Integer> {
}
