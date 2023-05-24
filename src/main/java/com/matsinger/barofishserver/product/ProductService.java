package com.matsinger.barofishserver.product;


import com.matsinger.barofishserver.category.Category;
import com.matsinger.barofishserver.category.CategoryRepository;
import com.matsinger.barofishserver.category.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Product> selectProductList() {
        return productRepository.findAll();
    }

    public Product selectProduct(Integer id) {
        return productRepository.findById(id).orElseThrow(() -> {
            throw new Error("상품 정보를 찾을 수 없습니다.");
        });
    }

    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    public Product update(Integer id, Product data) {
        productRepository.findById(id).orElseThrow(() -> {
            throw new Error("상품 정보를 찾을 수 없습니다.");
        });
        return productRepository.save(data);
    }

    public List<Product> searchProduct(String keyword) {
        return productRepository.findByTitleContainsAndStateEquals(keyword, ProductState.ACTIVE);
    }

}
