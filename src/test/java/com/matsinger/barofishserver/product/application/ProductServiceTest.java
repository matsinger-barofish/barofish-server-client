package com.matsinger.barofishserver.product.application;

import com.matsinger.barofishserver.domain.category.repository.CategoryRepository;
import com.matsinger.barofishserver.domain.product.application.ProductService;
import com.matsinger.barofishserver.domain.product.domain.Product;
import com.matsinger.barofishserver.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;

@SpringBootTest
@ActiveProfiles("local")
@Transactional
class ProductServiceTest {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired private CategoryRepository categoryRepository;

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

    @DisplayName("sample test")
    @Test
    void testMethodNameHere() {
        // given

        // when
        System.out.println(LocalDateTime.now());
        System.out.println(Calendar.getInstance());
        // then
    }

//    @DisplayName("상품의 적립률이 퍼센트로 온 경우 /100으로 DB에 저장돼야 한다.")
//    @Test
//    void productPointRateTest() {
//        // given
//        Category findCategory = categoryRepository.findById(2).get();
//        Product createdProduct = Product.builder()
//                .storeId(10000)
//                .category(findCategory)
//                .state(ProductState.ACTIVE)
//                .images("[]")
//                .title("테스트 상품")
//                .originPrice(0)
//                .discountRate(0)
//                .deliveryInfo("")
//                .deliverFeeType(ProductDeliverFeeType.FREE)
//                .minOrderPrice(0)
//                .descriptionImages("")
//                .expectedDeliverDay(1)
//                .needTaxation(false)
//                .representOptionItemId(10000)
//                .deliverBoxPerAmount(0)
//                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
//                .build();
//        createdProduct.setPointRate( (float) 10);
//        productRepository.save(createdProduct);
//        // when
//
//        // then
//    }
}