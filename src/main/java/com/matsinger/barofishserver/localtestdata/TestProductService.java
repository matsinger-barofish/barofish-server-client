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

    public void createProduct() {
        for (int i = 1; i < 3; i++) {
            Store findStore = storeRepository.findByLoginId("test" + i);

            Category findCategory = categoryRepository.findByName("testCategory" + i).get();

            boolean isProductPresent = productRepository.findByTitle("testProduct" + i).isPresent();
            if (!isProductPresent) {
                Product createdProduct = Product.builder()
                        .store(findStore)
                        .category(findCategory)
                        .state(ProductState.ACTIVE)
                        .images("image" + i)
                        .title("testProduct" + i)
                        .originPrice(1000 * i)
                        .discountRate(1)
                        .deliveryInfo("test" + i)
                        .descriptionImages("test" + i)
                        .createdAt(Timestamp.valueOf(LocalDateTime.now())).build();

                ProductType type = ProductType.builder()
                        .field("testType" + i).build();
                createdProduct.setProductType(type);

                ProductLocation location = ProductLocation.builder()
                        .field("testLocation" + i).build();
                createdProduct.setProductLocation(location);

                ProductProcess process = ProductProcess.builder()
                        .field("testProcess" + i).build();
                createdProduct.setProductProcess(process);

                ProductStorage storage = ProductStorage.builder()
                        .field("testStorage" + i).build();
                createdProduct.setProductStorage(storage);

                ProductUsage usage = ProductUsage.builder()
                        .field("testLocation" + i).build();
                createdProduct.setProductUsage(usage);

                productRepository.save(createdProduct);

                productLocationRepository.save(location);
                productProcessRepository.save(process);
                productStorageRepository.save(storage);
                productTypeRepository.save(type);
                productUsageRepository.save(usage);

            }

        }
    }
}
