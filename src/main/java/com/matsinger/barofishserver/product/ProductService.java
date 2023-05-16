package com.matsinger.barofishserver.product;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public Product selectProduct(Integer id) {
        return productRepository.findById(id).orElseThrow(() -> {
            throw new Error("상품 정보를 찾을 수 없습니다.");
        });
    }

}
