package com.matsinger.barofishserver.localtestdata;

import com.matsinger.barofishserver.product.OptionItem;
import com.matsinger.barofishserver.product.Product;
import com.matsinger.barofishserver.userauth.UserAuth;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Profile("local1")
@Component
@RequiredArgsConstructor
public class Local1DataInit {

    private final TestUserService testUserService;
    private final TestOrderService testOrderService;
    private final TestCategoryService testCategoryService;
    private final TestStoreService testStoreService;
    private final TestPaymentService testPaymentService;
    private final TestOptionService testOptionService;
    private final TestProductService testProductService;

    public static final List<String> suffixes = List.of("A", "B", "C", "D");
    @Transactional
    @PostConstruct
    public void initData() {

        testCategoryService.createTestCategory();
        testStoreService.createTestStore();

        Product productA = testProductService.createProduct(1000, "A");
        Product productB = testProductService.createProduct(1000, "B");

        OptionItem optionItemA = testOptionService.createOptions(productA, 2000, 10000, "A");
        OptionItem optionItemB = testOptionService.createOptions(productA, 3000, 10, "B");
        OptionItem optionItemC = testOptionService.createOptions(productB, 4000, 10000, "C");
        OptionItem optionItemD = testOptionService.createOptions(productB, 5000, 10000, "D");

        UserAuth userA = testUserService.createUser("A");
        UserAuth userB = testUserService.createUser("B");
        UserAuth userC = testUserService.createUser("C");
        UserAuth userD = testUserService.createUser("D");

        testOrderService.createTestOrder();


        testPaymentService.createPayment();
    }
}
