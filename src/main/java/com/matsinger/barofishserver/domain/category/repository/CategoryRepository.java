package com.matsinger.barofishserver.domain.category.repository;

import com.matsinger.barofishserver.domain.category.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    List<Category> findAllByCategoryId(Integer id);

    List<Category> findAllByCategoryIdIsNull();

    Optional<Category> findFirstByName(String categoryName);

    Optional<Category> findFirstByNameAndCategoryId(String categoryName, Integer categoryId);
}
