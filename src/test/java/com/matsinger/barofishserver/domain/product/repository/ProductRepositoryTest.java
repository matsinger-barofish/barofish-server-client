package com.matsinger.barofishserver.domain.product.repository;

import com.matsinger.barofishserver.domain.product.domain.ProductState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("local")
class ProductRepositoryTest {

    @Autowired private ProductRepository productRepository;

    @DisplayName("existsByIdAndState")
    @Test
    void existsByIdAndState() {
        // given
        productRepository.existsByIdAndState(10000, ProductState.ACTIVE);
        // when

        // then
    }
}