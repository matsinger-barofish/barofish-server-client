package com.matsinger.barofishserver.localtestdata;

import com.matsinger.barofishserver.category.Category;
import com.matsinger.barofishserver.order.OrderProductInfo;
import com.matsinger.barofishserver.order.dto.request.OrderReqProductInfoDto;
import com.matsinger.barofishserver.order.dto.request.OrderReqProductOptionDto;
import com.matsinger.barofishserver.order.dto.request.OrderRequestDto;
import com.matsinger.barofishserver.product.Option;
import com.matsinger.barofishserver.product.OptionItem;
import com.matsinger.barofishserver.product.Product;
import com.matsinger.barofishserver.store.Store;
import com.matsinger.barofishserver.user.User;
import com.matsinger.barofishserver.userauth.UserAuth;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Profile("local1")
@Component
@RequiredArgsConstructor
public class Local1DataInit {

    private final TestUserService testUserService;
//    private final TestOrderService testOrderService;
    private final TestCategoryService testCategoryService;
    private final TestStoreService testStoreService;
    private final TestPaymentService testPaymentService;
    private final TestOptionService testOptionService;
    private final TestProductService testProductService;

    public static final List<String> suffixes = List.of("A", "B", "C", "D");
    @Transactional
    @PostConstruct
    public void initData() {

        Category categoryA = testCategoryService.createTestCategory(0, "A");
        Store storeA = testStoreService.createTestStore(1, "A");
        Store storeB = testStoreService.createTestStore(2, "B");

        Product productA = testProductService.createProduct(1, 1000, "A", "A");
        Product productB = testProductService.createProduct(2,1000, "B", "A");

        OptionItem optionItemA = testOptionService.createOptions(1, productA, 2000, 10000, "A");
        OptionItem optionItemB = testOptionService.createOptions(2, productA, 3000, 10, "B");
        OptionItem optionItemC = testOptionService.createOptions(3, productB, 4000, 10000, "A");
        OptionItem optionItemD = testOptionService.createOptions(4, productB, 5000, 10000, "B");

        UserAuth userA = testUserService.createUser(1, "A");
        UserAuth userB = testUserService.createUser(2, "B");
        UserAuth userC = testUserService.createUser(3, "C");
        UserAuth userD = testUserService.createUser(4, "D");

//        testOrderService.createTestOrder();
//
//
//        testPaymentService.createPayment();
    }

//    private OrderRequestDto createOrderRequest(UserAuth user, List<Product> products, List<OptionItem> options,
//                                               int optionId, int optionCount) {
//
//        List<OrderReqProductInfoDto> productInfoDtos = new ArrayList<>();
//        for (Product product : products) {
//
//            List<OrderReqProductOptionDto> optionDtos = new ArrayList<>();
//            for (Option option : product.getOptions()) {
//
//                OrderReqProductOptionDto createdOptionDto = OrderReqProductOptionDto.builder()
//                        .optionId(optionId)
//                        .amount(optionCount).build();
//                optionDtos.add(createdOptionDto);
//            }
//
//            OrderReqProductInfoDto createdProductInfo = OrderReqProductInfoDto.builder()
//                    .productId(product.getId())
//                    .storeId(product.getStoreId())
//                    .options(optionDtos).build();
//            productInfoDtos.add(createdProductInfo);
//        }
//
//        return OrderRequestDto.builder()
//                .userId(user.getUserId())
//                .products(productInfoDtos).build();
//    }
}
