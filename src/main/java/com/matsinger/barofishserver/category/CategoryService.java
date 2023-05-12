package com.matsinger.barofishserver.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class CategoryService {

    @Autowired
    private final CategoryRepository categoryRepository;


    public List<Category> findAll(Long id) {
        if (id != null) return categoryRepository.findAllByCategoryId(id);
        return categoryRepository.findAll();
    }

    public Category findById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new Error("카테고리 정보를 찾을 수 없습니다."));
    }

    public Category add(Category category) {
        return categoryRepository.save(category);
    }

    public Boolean delete(Long id) {
        try {
            categoryRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
