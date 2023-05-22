//package com.matsinger.barofishserver.localtestdata;
//
//import com.matsinger.barofishserver.category.Category;
//import com.matsinger.barofishserver.category.CategoryRepository;
//import com.matsinger.barofishserver.order.dto.OrderProductInfoDto;
//import com.matsinger.barofishserver.order.dto.OrderProductOptionDto;
//import com.matsinger.barofishserver.order.dto.request.OrderRequestDto;
//import com.matsinger.barofishserver.order.service.OrderCommandService;
//import com.matsinger.barofishserver.product.Product;
//import com.matsinger.barofishserver.product.ProductRepository;
//import com.matsinger.barofishserver.product.ProductState;
//import com.matsinger.barofishserver.store.Store;
//import com.matsinger.barofishserver.store.StoreRepository;
//import com.matsinger.barofishserver.store.StoreState;
//import com.matsinger.barofishserver.user.User;
//import com.matsinger.barofishserver.user.UserRepository;
//import com.matsinger.barofishserver.user.UserState;
//import com.matsinger.barofishserver.userauth.LoginType;
//import com.matsinger.barofishserver.userauth.UserAuth;
//import com.matsinger.barofishserver.userauth.UserAuthRepository;
//import jakarta.annotation.PostConstruct;
//import jakarta.persistence.EntityManager;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Profile;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.sql.Timestamp;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//@Slf4j
//@Profile("local1")
//@Component
//@RequiredArgsConstructor
//public class Local1DataInit {
//
//    private final TestUserService testUserService;
//    private final TestOrderService testOrderService;
//    private final TestProductService testProductService;
//    private final TestCategoryService testCategoryService;
//    private final TestStoreService testStoreService;
//
//    @Transactional
//    @PostConstruct
//    public void initData() {
//        testCategoryService.createTestCategory();
//        testStoreService.createTestStore();
//        testProductService.createProduct();
//        testUserService.createUser();
//        testOrderService.createTestOrders();
//    }
//
//    @Slf4j
//    @Component
//    @Transactional
//    @RequiredArgsConstructor
//    static class TestUserService {
//        private final UserRepository userRepository;
//        private final UserAuthRepository userAuthRepository;
//        private final EntityManager em;
//
//        public void createUser() {
//            for (int i = 1; i < 3; i++) {
//                User createdUser = User.builder()
//                        .state(UserState.ACTIVE)
//                        .joinAt(Timestamp.valueOf(LocalDateTime.now())).build();
//
//                UserAuth createdUserAuth = UserAuth.builder()
//                        .loginType(LoginType.IDPW)
//                        .loginId("test" + i)
//                        .password("test" + i).build();
//
//                createdUserAuth.setUser(createdUser);
//                userRepository.save(createdUser);
//                userAuthRepository.save(createdUserAuth);
//            }
//        }
//    }
//
//    @Component
//    @RequiredArgsConstructor
//    static class TestProductService {
//
//        private final ProductRepository productRepository;
//        private final StoreRepository storeRepository;
//
//        public void createProduct() {
//            for (int i = 1; i < 3; i++) {
//                Store findStore = storeRepository.findById(i).get();
//                Product createdProduct = Product.builder()
//                        .store(findStore)
//                        .categoryId(i)
//                        .state(ProductState.ACTIVE)
//                        .images("image" + i)
//                        .title("test" + i)
//                        .originPrice(1000 * i)
//                        .discountRate(1)
//                        .deliveryInfo("test" + i)
//                        .descriptionImages("test" + i)
//                        .createdAt(Timestamp.valueOf(LocalDateTime.now())).build();
//                productRepository.save(createdProduct);
//            }
//        }
//    }
//
//    @Component
//    @RequiredArgsConstructor
//    static class TestCategoryService {
//
//        private final CategoryRepository categoryRepository;
//
//        public void createTestCategory() {
//            for (int i = 1; i < 3; i++) {
//                Category createdCategory = Category.builder()
//                        .categoryId(i)
//                        .image("test" + i)
//                        .name("test" + i).build();
//                categoryRepository.save(createdCategory);
//            }
//        }
//    }
//
//    @Component
//    @RequiredArgsConstructor
//    static class TestOrderService {
//
//        private final OrderCommandService orderCommandService;
//
//        public void createTestOrders() {
//            int seq = 1;
//            List<OrderProductInfoDto> products = new ArrayList<>();
//            for (int i = 1; i < 3; i++) {
//                List<OrderProductOptionDto> options = new ArrayList<>();
//
//                for (int j = 1; j < 3; j++) {
//                    OrderProductOptionDto optionDto = OrderProductOptionDto.builder()
//                            .optionId(seq)
//                            .optionName("testOption" + seq)
//                            .optionPrice(1000 * seq).build();
//                    options.add(optionDto);
//                    seq++;
//                }
//                OrderProductInfoDto productInfoDto = OrderProductInfoDto.builder()
//                        .productId(i)
//                        .originPrice(5000 * i)
//                        .discountRate(i * 0.1)
//                        .amount(10 + i)
//                        .deliveryFee(1000 * i)
//                        .options(options).build();
//                products.add(productInfoDto);
//            }
//
//            OrderRequestDto orderRequest = OrderRequestDto.builder()
//                    .userId("test1")
//                    .totalPrice(100000)
//                    .products(products).build();
//
//            orderCommandService.createOrderSheet(orderRequest);
//        }
//    }
//
//    @Component
//    @RequiredArgsConstructor
//    static class TestStoreService {
//        private final StoreRepository storeRepository;
//
//        public void createTestStore() {
//            for (int i = 1; i < 3; i++) {
//                Store createdStore = Store.builder()
//                        .state(StoreState.ACTIVE)
//                        .loginId("test" + i)
//                        .password("test" + i)
//                        .joinAt(Timestamp.valueOf(LocalDateTime.now())).build();
//                storeRepository.save(createdStore);
//            }
//        }
//
//    }
//}
