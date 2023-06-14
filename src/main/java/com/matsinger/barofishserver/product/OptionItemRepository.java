package com.matsinger.barofishserver.product;

import com.matsinger.barofishserver.product.object.OptionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface OptionItemRepository extends JpaRepository<OptionItem, Integer> {
    Optional<OptionItem> findByName(String name);

    @Transactional
    void deleteAllByOptionId(Integer optionId);
    List<OptionItem> findAllByOptionId(Integer optionId);
}
