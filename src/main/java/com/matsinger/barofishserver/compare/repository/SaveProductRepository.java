package com.matsinger.barofishserver.compare.repository;

import com.matsinger.barofishserver.compare.domain.SaveProduct;
import com.matsinger.barofishserver.compare.domain.SaveProductId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SaveProductRepository extends JpaRepository<SaveProduct, SaveProductId> {
    public List<SaveProduct> findAllByUserId(Integer userId);


}
