package com.matsinger.barofishserver.product.application;

import com.matsinger.barofishserver.product.domain.Product;
import com.matsinger.barofishserver.product.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Timestamp;
import java.util.List;

@SpringBootTest
@ActiveProfiles("local")
class ProductServiceTest {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;

    @DisplayName("상품의 프로모션 기간이 지난 경우 쿼리 테스트")
    @Test
    void productPromotionTest() {
        // given
        Product product1 = productRepository.findById(1).get(); // 09-11 ~ 09-12 -> 프로모션 활션화
        Product product2 = productRepository.findById(2).get(); // 09-08 ~ 09-10 -> 프로모션 비활성화

        // when
        List<Product> allByPromotionStartAtBefore = productRepository.findAllByPromotionStartAtBeforeAndPromotionEndAtAfter(new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()));
        List<Product> allByPromotionEndAtBefore = productRepository.findAllByPromotionEndAtBefore(new Timestamp(System.currentTimeMillis()));
        // then
        for (Product product : allByPromotionStartAtBefore) {
            System.out.println("대게 테스트1이 나와야 함 -> " + product.getTitle());
        }

        for (Product product : allByPromotionEndAtBefore) {
            System.out.println("안녕이 나와야 함 -> " + product.getTitle());
        }

    }
}