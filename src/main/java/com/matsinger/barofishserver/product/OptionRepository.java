package com.matsinger.barofishserver.product;

import com.matsinger.barofishserver.product.object.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OptionRepository extends JpaRepository<Option, Integer> {
    List<Option> findAllByProductId(Integer productId);
    @Transactional
    void deleteAllByProductId(Integer productId);
}
