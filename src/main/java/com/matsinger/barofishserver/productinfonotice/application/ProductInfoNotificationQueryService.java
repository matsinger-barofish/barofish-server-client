package com.matsinger.barofishserver.productinfonotice.application;

import com.matsinger.barofishserver.category.application.CategoryQueryService;
import com.matsinger.barofishserver.product.application.ProductQueryService;
import com.matsinger.barofishserver.product.domain.Product;
import com.matsinger.barofishserver.productinfonotice.domain.AgriculturalAndLivestockProductsInfo;
import com.matsinger.barofishserver.productinfonotice.domain.ProcessedFoodInfo;
import com.matsinger.barofishserver.productinfonotice.domain.ProductInfoNoticeManager;
import com.matsinger.barofishserver.productinfonotice.domain.ProductInformation;
import com.matsinger.barofishserver.productinfonotice.repository.AgriculturalAndLivestockProductsInfoRepository;
import com.matsinger.barofishserver.productinfonotice.repository.ProcessedFoodInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.matsinger.barofishserver.productinfonotice.domain.ProductInfoNoticeManager.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductInfoNotificationQueryService {

    private final ProductQueryService productQueryService;
    private final AgriculturalAndLivestockProductsInfoRepository agriculturalAndLivestockProductsInfoRepository;
    private final ProcessedFoodInfoRepository processedFoodInfoRepository;

    public ProductInformation getProductInfoNotificationForm(String itemCode) {
        return getProductInformationForm(itemCode);
    }

    public ProductInformation getProductInfoNotification(int productId) {
        Product findProduct = productQueryService.findById(productId);

        if (findProduct.getItemCode().equals(LIVESTOCK.getItemCode())) {
            AgriculturalAndLivestockProductsInfo findInfo = findLivestockProductsInfoByProductId(productId);
            return findInfo.toDto();
        }
        if (findProduct.getItemCode().equals(PROCESSED.getItemCode())) {
            ProcessedFoodInfo findInfo = findProcessedFoodInfoByProductId(productId);
            return findInfo.toDto();
        }

        throw new IllegalArgumentException("상품 고시 정보를 찾을 수 없습니다.");
    }

    public ProcessedFoodInfo findProcessedFoodInfoByProductId(int productId) {
        return processedFoodInfoRepository.findByProductId(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품 고시 정보를 찾을 수 없습니다."));
    }

    public AgriculturalAndLivestockProductsInfo findLivestockProductsInfoByProductId(int productId) {
        return agriculturalAndLivestockProductsInfoRepository.findByProductId(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품 고시 정보를 찾을 수 없습니다."));
    }
}
