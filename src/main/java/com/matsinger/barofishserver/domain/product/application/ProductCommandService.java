package com.matsinger.barofishserver.domain.product.application;

import com.matsinger.barofishserver.domain.product.domain.Product;
import com.matsinger.barofishserver.domain.product.domain.ProductState;
import com.matsinger.barofishserver.domain.store.domain.StoreState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductCommandService {

    private final ProductQueryService productQueryService;

    public void convertStoreProductsStateWhenPartnerUpdate(int storeId, StoreState state) {
        if (state.equals(StoreState.BANNED)) {
            List<Product> activeProducts = productQueryService.findAllActiveProductsByStoreId(storeId);
            activeProducts.forEach(v -> v.setState(ProductState.INACTIVE_PARTNER));
        }
        if (state.equals(StoreState.ACTIVE)) {
            List<Product> temporaryInactiveProducts = productQueryService.findAllTemporaryInactiveProductsByStoreId(storeId);
            temporaryInactiveProducts.forEach(v -> v.setState(ProductState.ACTIVE));
        }
    }
}
