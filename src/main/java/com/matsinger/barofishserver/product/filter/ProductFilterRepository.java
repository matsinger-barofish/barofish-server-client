package com.matsinger.barofishserver.product.filter;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductFilterRepository extends JpaRepository<ProductFilterValue, ProductFilterValueId> {
    void deleteAllByCompareFilterId(Integer compareFilterId);

    List<ProductFilterValue> findAllByProductId(Integer productId);

    void deleteAllByProductId(Integer productId);
}
