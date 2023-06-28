package com.matsinger.barofishserver.category;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryFilterRepository extends JpaRepository<CategoryFilterMap, CategoryFilterId> {
    void deleteAllByCategoryId(Integer categoryId);

    void deleteAllByCompareFilterId(Integer compareFilterId);

    List<CategoryFilterMap> findAllByCategoryId(Integer categoryId);
}
