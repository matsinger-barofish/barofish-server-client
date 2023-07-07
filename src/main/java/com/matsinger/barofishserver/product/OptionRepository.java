package com.matsinger.barofishserver.product;

import com.matsinger.barofishserver.product.object.Option;
import com.matsinger.barofishserver.product.object.OptionState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OptionRepository extends JpaRepository<Option, Integer> {
    List<Option> findAllByProductIdAndState(Integer productId, OptionState state);
    @Transactional
    void deleteAllByProductId(Integer productId);
}
