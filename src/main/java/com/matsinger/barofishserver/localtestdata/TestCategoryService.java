package com.matsinger.barofishserver.localtestdata;

import com.matsinger.barofishserver.category.Category;
import com.matsinger.barofishserver.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@Transactional
@RequiredArgsConstructor
public class TestCategoryService {

    private final CategoryRepository categoryRepository;

    public Category createTestCategory(int categoryId, String suffix) {

        boolean isParentCategoryPresent = categoryRepository.findByName("testParentCategory" + suffix).isPresent();
        if (!isParentCategoryPresent) {
            Category parentCategory = Category.builder()
                    .image("testParentCategory" + suffix)
                    .name("testParentCategory" + suffix).build();

            Category testCategory = Category.builder()
                    .categoryId(categoryId)
                    .parentCategory(parentCategory)
                    .image("test" + suffix)
                    .name("testCategory" + suffix).build();

            categoryRepository.save(parentCategory);
            categoryRepository.save(testCategory);
            return testCategory;
        }
        return null;
    }
}
