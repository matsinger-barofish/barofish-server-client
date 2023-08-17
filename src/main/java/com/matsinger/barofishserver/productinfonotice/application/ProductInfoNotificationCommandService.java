package com.matsinger.barofishserver.productinfonotice.application;

import com.matsinger.barofishserver.product.application.ProductQueryService;
import com.matsinger.barofishserver.product.domain.Product;
import com.matsinger.barofishserver.product.option.domain.Option;
import com.matsinger.barofishserver.productinfonotice.domain.AgriculturalAndLivestockProductsInfo;
import com.matsinger.barofishserver.productinfonotice.domain.ProcessedFoodInfo;
import com.matsinger.barofishserver.productinfonotice.domain.ProductInformation;
import com.matsinger.barofishserver.productinfonotice.repository.AgriculturalAndLivestockProductsInfoRepository;
import com.matsinger.barofishserver.productinfonotice.repository.ProcessedFoodInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.matsinger.barofishserver.productinfonotice.domain.ProductInfoNoticeManager.LIVESTOCK;
import static com.matsinger.barofishserver.productinfonotice.domain.ProductInfoNoticeManager.PROCESSED;

@Service
@RequiredArgsConstructor
public class ProductInfoNotificationCommandService {
    private final ProductQueryService productQueryService;
    private final AgriculturalAndLivestockProductsInfoRepository agriculturalAndLivestockProductsInfoRepository;
    private final ProcessedFoodInfoRepository processedFoodInfoRepository;

    public void addProductInfoNotification(ProductInformation request) {
        Product findProduct = productQueryService.findById(request.getProductId());
        verifyIfProductInfoNotificationAlreadyExists(findProduct);
        findProduct.setItemCode(request.getItemCode());

        if (request.getItemCode().equals(LIVESTOCK.getItemCode())) {
            agriculturalAndLivestockProductsInfoRepository.save((AgriculturalAndLivestockProductsInfo) request.toEntity(findProduct));
            return;
        }
        if (request.getItemCode().equals(PROCESSED.getItemCode())) {
            processedFoodInfoRepository.save((ProcessedFoodInfo) request.toEntity(findProduct));
            return;
        }

        throw new IllegalArgumentException("올바르지 않은 품목 코드입니다.");
    }

    private void verifyIfProductInfoNotificationAlreadyExists(Product findProduct) {

        Optional<String> optionalItemCode = Optional.ofNullable(findProduct.getItemCode());
        if (optionalItemCode.isEmpty()) {
            return;
        }

        String itemCode = optionalItemCode.get();

        if (itemCode.equals(LIVESTOCK.getItemCode())) {
            if (agriculturalAndLivestockProductsInfoRepository.findByProductId(findProduct.getId()).isPresent()) {
                throw new IllegalArgumentException("이미 상품 품목 코드가 존재합니다.");
            }
        }
        if (itemCode.equals(PROCESSED.getItemCode())) {
            if (processedFoodInfoRepository.findByProductId(findProduct.getId()).isPresent()) {
                throw new IllegalArgumentException("이미 상품 품목 코드가 존재합니다.");
            }
        }
    }
}
