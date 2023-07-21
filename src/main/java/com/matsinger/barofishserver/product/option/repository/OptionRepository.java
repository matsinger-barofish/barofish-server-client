package com.matsinger.barofishserver.product.option.repository;

import com.matsinger.barofishserver.product.domain.OptionState;
import com.matsinger.barofishserver.product.option.domain.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OptionRepository extends JpaRepository<Option, Integer> {
    List<Option> findAllByProductIdAndState(Integer productId, OptionState state);

    Option findFirstByProductIdAndIsNeededTrue(Integer productId);

    @Transactional
    void deleteAllByProductId(Integer productId);
}
