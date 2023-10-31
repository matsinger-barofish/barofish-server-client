package com.matsinger.barofishserver.domain.basketProduct.repository;

import com.matsinger.barofishserver.domain.basketProduct.domain.BasketProductOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BasketProductOptionRepository extends JpaRepository<BasketProductOption, Integer> {
    List<BasketProductOption> findAllByOrderProductId(Integer orderProductId);

    @Transactional
    void deleteAllByOrderProductIdIn(List<Integer> ids);

    void deleteAllByOptionId(Integer optionId);

    void deleteAllByOptionIdIn(List<Integer> optionIds);

//    void deleteAllByOrderProductIdIn(List<Integer> ids);
}
