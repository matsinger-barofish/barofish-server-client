package com.matsinger.barofishserver.domain.productinfonotice.repository;

import com.matsinger.barofishserver.domain.productinfonotice.domain.AgriculturalAndLivestockProductsInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AgriculturalAndLivestockProductsInfoRepository extends JpaRepository<AgriculturalAndLivestockProductsInfo, Integer> {
    Optional<AgriculturalAndLivestockProductsInfo> findByProductId(int productId);

    int deleteByProductId(int productId);
}
