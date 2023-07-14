package com.matsinger.barofishserver.category;

import com.matsinger.barofishserver.compare.filter.CompareFilterDto;
import com.matsinger.barofishserver.compare.filter.CompareFilterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class CategoryService {

    @Autowired
    private final CategoryRepository categoryRepository;
    private final CategoryFilterService categoryFilterService;
    private final CategoryFilterRepository categoryFilterRepository;
    private final CompareFilterService compareFilterService;


    public List<Category> findParentCategories() {
        return categoryRepository.findAllByCategoryIdIsNull();
    }

    public List<Category> findAll(Integer id) {
        if (id != null) return categoryRepository.findAllByCategoryId(id);
        return categoryRepository.findAll();
    }

    public Category findById(Integer id) {
        return categoryRepository.findById(id).orElseThrow(() -> new Error("카테고리 정보를 찾을 수 없습니다."));
    }

    public Category add(Category category) {
        return categoryRepository.save(category);
    }

    public Category update(Integer id, Category data) {
        Category category = categoryRepository.findById(id).get();
        if (data.getName() != null) category.setName(data.getName());
        if (data.getImage() != null) category.setImage(data.getImage());
        categoryRepository.save(category);
        return category;
    }

    @Transactional
    public Boolean delete(Integer id) {
        try {
            categoryFilterRepository.deleteAllByCategoryId(id);
            categoryRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public CategoryDto convert2Dto(Category data) {
        List<CategoryDto> categories = new ArrayList<>();
        String parentName = null;
        if (data.getCategoryId() == null) {
            for (Category category : data.getCategoryList()) {
                categories.add(convert2Dto(category));
            }
            List<CompareFilterDto>
                    compareFilterDtos =
                    categoryFilterService.selectCompareFilterIdList(data.getId()).stream().map(v -> compareFilterService.selectCompareFilter(
                            v).convert2Dto()).toList();
        } else {
            parentName = data.getParentCategory().getName();
        }
        return CategoryDto.builder().id(data.getId()).parentId(data.getCategoryId()).name(data.getName()).image(data.getImage()).ParentCategoryName(
                parentName).categories(categories).build();
    }

    public Optional<Category> findOptionalCategoryWithName(String name) {
        return categoryRepository.findFirstByName(name);
    }
    public Optional<Category> findOptionalCategoryWithName(String name, Integer parentCategoryId) {
        return categoryRepository.findFirstByNameAndCategoryId(name, parentCategoryId);
    }

}
