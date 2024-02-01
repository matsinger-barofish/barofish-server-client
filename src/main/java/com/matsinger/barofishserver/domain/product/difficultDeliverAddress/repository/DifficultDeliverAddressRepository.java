package com.matsinger.barofishserver.domain.product.difficultDeliverAddress.repository;

import com.matsinger.barofishserver.domain.product.difficultDeliverAddress.domain.DifficultDeliverAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DifficultDeliverAddressRepository extends JpaRepository<DifficultDeliverAddress, Integer> {
    List<DifficultDeliverAddress> findAllByProductId(Integer productId);

    void deleteAllByProductId(Integer productId);
}
