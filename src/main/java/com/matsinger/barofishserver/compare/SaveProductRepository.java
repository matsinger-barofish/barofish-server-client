package com.matsinger.barofishserver.compare;

import com.matsinger.barofishserver.compare.obejct.SaveProduct;
import com.matsinger.barofishserver.compare.obejct.SaveProductId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SaveProductRepository extends JpaRepository<SaveProduct, SaveProductId> {
    public List<SaveProduct> findAllByUserId(Integer userId);


}
