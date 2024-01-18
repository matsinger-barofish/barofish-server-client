package com.matsinger.barofishserver.domain.productinfonotice.repository;

import com.matsinger.barofishserver.domain.productinfonotice.domain.ProcessedFoodInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProcessedFoodInfoRepository extends JpaRepository<ProcessedFoodInfo, Integer> {
    Optional<ProcessedFoodInfo> findByProductId(int productId);

    int deleteByProductId(int productId);
}
