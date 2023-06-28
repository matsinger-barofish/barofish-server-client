package com.matsinger.barofishserver.basketProduct;

import com.matsinger.barofishserver.basketProduct.obejct.BasketProductInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BasketProductInfoRepository extends JpaRepository<BasketProductInfo, Integer> {
    List<BasketProductInfo> findAllByUserId(Integer userId);
    List<BasketProductInfo> findByUserIdAndProductId(Integer userId, Integer productId);
    void deleteAllByIdIn(List<Integer> ids);
}
