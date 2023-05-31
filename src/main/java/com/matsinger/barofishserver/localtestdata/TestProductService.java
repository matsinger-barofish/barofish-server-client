package com.matsinger.barofishserver.localtestdata;

import com.matsinger.barofishserver.category.Category;
import com.matsinger.barofishserver.category.CategoryRepository;
import com.matsinger.barofishserver.product.Product;
import com.matsinger.barofishserver.product.ProductRepository;
import com.matsinger.barofishserver.product.ProductState;
import com.matsinger.barofishserver.product.productinfo.*;
import com.matsinger.barofishserver.store.Store;
import com.matsinger.barofishserver.store.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TestProductService {

        private final ProductRepository productRepository;
        private final StoreRepository storeRepository;
        private final CategoryRepository categoryRepository;

        private final ProductLocationRepository productLocationRepository;
        private final ProductProcessRepository productProcessRepository;
        private final ProductStorageRepository productStorageRepository;
        private final ProductTypeRepository productTypeRepository;
        private final ProductUsageRepository productUsageRepository;

        public static final List<String> suffixes = List.of("A", "B", "C", "D");

        public Product createProduct(int id, int price, String productSuffix, Store store) {

                Store findStore = storeRepository.findById(store.getId()).get();
                Category findCategory = categoryRepository.findByName("testCategory" + "A").get();

                Product createdProduct = Product.builder()
                                .id(id)
                                .store(findStore)
                                .category(findCategory)
                                .state(ProductState.ACTIVE)
                                .images("image" + productSuffix)
                                .title("product" + productSuffix)
                                .originPrice(price)
                                .discountRate(1)
                                .deliveryInfo("test" + productSuffix)
                                .descriptionImages("test" + productSuffix)
                                .createdAt(Timestamp.valueOf(LocalDateTime.now())).build();

                ProductType type = ProductType.builder()
                                .field("testType" + productSuffix).build();
                createdProduct.setProductType(type);

                ProductLocation location = ProductLocation.builder()
                                .field("testLocation" + productSuffix).build();
                createdProduct.setProductLocation(location);

                ProductProcess process = ProductProcess.builder()
                                .field("testProcess" + productSuffix).build();
                createdProduct.setProductProcess(process);

                ProductStorage storage = ProductStorage.builder()
                                .field("testStorage" + productSuffix).build();
                createdProduct.setProductStorage(storage);

                ProductUsage usage = ProductUsage.builder()
                                .field("testLocation" + productSuffix).build();
                createdProduct.setProductUsage(usage);

                productRepository.save(createdProduct);

                productLocationRepository.save(location);
                productProcessRepository.save(process);
                productStorageRepository.save(storage);
                productTypeRepository.save(type);
                productUsageRepository.save(usage);
                return createdProduct;
        }
}
