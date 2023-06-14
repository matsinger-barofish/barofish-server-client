package com.matsinger.barofishserver.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class CategoryService {

    @Autowired
    private final CategoryRepository categoryRepository;


    public List<Category> findParentCategories(){
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

    public Boolean delete(Integer id) {
        try {
            categoryRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
