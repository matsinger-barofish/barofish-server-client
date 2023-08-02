package com.matsinger.barofishserver.category.filter.application;

import com.matsinger.barofishserver.category.filter.domain.CategoryFilterMap;
import com.matsinger.barofishserver.category.filter.repository.CategoryFilterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
@RequiredArgsConstructor
@Slf4j
@Service
public class CategoryFilterQueryService {
    private final CategoryFilterRepository categoryFilterRepository;

    public List<Integer> selectCompareFilterIdList(Integer categoryId) {
        return categoryFilterRepository.findAllByCategoryId(categoryId).stream().map(CategoryFilterMap::getCompareFilterId).toList();
    }
}
