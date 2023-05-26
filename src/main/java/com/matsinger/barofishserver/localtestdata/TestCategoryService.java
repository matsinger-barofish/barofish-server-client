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

    public void createTestCategory() {

        for (int i = 1; i < 3; i++) {

            boolean isParentCategoryPresent = categoryRepository.findByName("testParentCategory" + i).isPresent();
            if (!isParentCategoryPresent) {
                Category parentCategory = Category.builder()
                        .image("testParentCategory" + i)
                        .name("testParentCategory" + i).build();

                Category testCategory = Category.builder()
                        .parentCategory(parentCategory)
                        .parentCategory(parentCategory)
                        .image("test" + i)
                        .name("testCategory" + i).build();

                categoryRepository.save(parentCategory);
                categoryRepository.save(testCategory);

            }
        }
    }
}
