package com.matsinger.barofishserver.productinfo.repository;

import com.matsinger.barofishserver.productinfo.domain.ProductProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductProcessRepository extends JpaRepository<ProductProcess, Integer> {
}
