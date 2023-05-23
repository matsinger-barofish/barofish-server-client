package com.matsinger.barofishserver.localtestdata;

import com.matsinger.barofishserver.category.Category;
import com.matsinger.barofishserver.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestCategoryService {

    private final CategoryRepository categoryRepository;

    public void createTestCategory() {
        for (int i = 1; i < 3; i++) {
            Category createdCategory = Category.builder()
                    .categoryId(i)
                    .image("test" + i)
                    .name("test" + i).build();
            categoryRepository.save(createdCategory);
        }
    }
}
