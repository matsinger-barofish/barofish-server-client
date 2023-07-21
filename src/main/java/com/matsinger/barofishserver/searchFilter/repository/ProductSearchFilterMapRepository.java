package com.matsinger.barofishserver.searchFilter.repository;

import com.matsinger.barofishserver.searchFilter.domain.ProductSearchFilterMap;
import com.matsinger.barofishserver.searchFilter.domain.ProductSearchFilterMapId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductSearchFilterMapRepository
        extends JpaRepository<ProductSearchFilterMap, ProductSearchFilterMapId> {
    boolean existsByFieldIdAndProductId(Integer fieldId, Integer productId);

    void deleteAllByFieldIdIn(List<Integer> ids);

    void deleteAllByProductId(Integer id);

    List<ProductSearchFilterMap> findAllByProductId(Integer productId);
}
