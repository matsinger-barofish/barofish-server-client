package com.matsinger.barofishserver.domain.basketProduct.repository;

import com.matsinger.barofishserver.domain.basketProduct.domain.BasketProductInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BasketProductInfoRepository extends JpaRepository<BasketProductInfo, Integer> {
    List<BasketProductInfo> findAllByUserId(Integer userId);

    List<BasketProductInfo> findAllByUserIdIn(List<Integer> userIds);

    List<BasketProductInfo> findByUserIdAndProductId(Integer userId, Integer productId);

    void deleteAllByIdIn(List<Integer> ids);

    Optional<BasketProductInfo> findByUserIdAndOptionItemId(Integer userId, Integer optionItemReqId);

    Optional<List<BasketProductInfo>> findAllByProductIdAndUserId(Integer userId, Integer productId);
}
