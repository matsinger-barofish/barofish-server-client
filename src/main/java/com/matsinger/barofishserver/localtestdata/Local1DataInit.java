package com.matsinger.barofishserver.localtestdata;

import com.matsinger.barofishserver.category.Category;
import com.matsinger.barofishserver.category.CategoryRepository;
import com.matsinger.barofishserver.order.dto.OrderProductInfoDto;
import com.matsinger.barofishserver.order.dto.OrderProductOptionDto;
import com.matsinger.barofishserver.order.dto.request.OrderRequestDto;
import com.matsinger.barofishserver.order.service.OrderCommandService;
import com.matsinger.barofishserver.product.Product;
import com.matsinger.barofishserver.product.ProductRepository;
import com.matsinger.barofishserver.product.ProductState;
import com.matsinger.barofishserver.store.Store;
import com.matsinger.barofishserver.store.StoreRepository;
import com.matsinger.barofishserver.store.StoreState;
import com.matsinger.barofishserver.user.User;
import com.matsinger.barofishserver.user.UserRepository;
import com.matsinger.barofishserver.user.UserState;
import com.matsinger.barofishserver.userauth.LoginType;
import com.matsinger.barofishserver.userauth.UserAuth;
import com.matsinger.barofishserver.userauth.UserAuthRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Profile("local1")
@Component
@RequiredArgsConstructor
public class Local1DataInit {

    private final TestOrderService testOrderService;
    private final TestProductService testProductService;
    private final TestCategoryService testCategoryService;
//    private final TestStoreInfoService testStoreInfoService;
    private final TestStoreService testStoreService;

    @Transactional
    @PostConstruct
    public void initData() {
        testCategoryService.createTestCategory();
        testStoreService.createTestStore();
        testProductService.createProduct();
        testOrderService.createTestOrders();
    }
}
