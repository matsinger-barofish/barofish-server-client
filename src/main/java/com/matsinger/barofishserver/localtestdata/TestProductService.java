package com.matsinger.barofishserver.localtestdata;

import com.matsinger.barofishserver.product.Product;
import com.matsinger.barofishserver.product.ProductRepository;
import com.matsinger.barofishserver.product.ProductState;
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

    public void createProduct() {
        for (int i = 1; i < 3; i++) {
            Store findStore = storeRepository.findById(i).get();
            Product createdProduct = Product.builder()
                    .store(findStore)
                    .categoryId(i)
                    .state(ProductState.ACTIVE)
                    .images("image" + i)
                    .title("test" + i)
                    .originPrice(1000 * i)
                    .discountRate(1)
                    .deliveryInfo("test" + i)
                    .descriptionImages("test" + i)
                    .createdAt(Timestamp.valueOf(LocalDateTime.now())).build();
            productRepository.save(createdProduct);
        }
    }
}
