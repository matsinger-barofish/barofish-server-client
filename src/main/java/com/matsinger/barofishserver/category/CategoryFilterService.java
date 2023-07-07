package com.matsinger.barofishserver.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.experimental.categories.Categories;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class CategoryFilterService {
    private final CategoryFilterRepository categoryFilterRepository;

    public List<Integer> selectCompareFilterIdList(Integer categoryId) {
        return categoryFilterRepository.findAllByCategoryId(categoryId).stream().map(CategoryFilterMap::getCompareFilterId).toList();
    }

    public void addCategoryFilterMap(CategoryFilterMap filter) {
        categoryFilterRepository.save(filter);
    }

    public void deleteCategoryFilterMap(CategoryFilterId id) {
        categoryFilterRepository.deleteById(id);
    }
}
