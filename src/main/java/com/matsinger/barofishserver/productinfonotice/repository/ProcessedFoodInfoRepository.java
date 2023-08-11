package com.matsinger.barofishserver.productinfonotice.repository;

import com.matsinger.barofishserver.productinfonotice.domain.ProcessedFoodInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedFoodInfoRepository extends JpaRepository<Integer, ProcessedFoodInfo> {
}
