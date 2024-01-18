package com.matsinger.barofishserver.domain.productinfonotice.application;

import com.matsinger.barofishserver.domain.productinfonotice.domain.AgriculturalAndLivestockProductsInfo;
import com.matsinger.barofishserver.domain.productinfonotice.domain.ProcessedFoodInfo;
import com.matsinger.barofishserver.domain.productinfonotice.domain.ProductInfoNoticeForm;
import com.matsinger.barofishserver.domain.productinfonotice.domain.ProductInformation;
import com.matsinger.barofishserver.domain.productinfonotice.repository.AgriculturalAndLivestockProductsInfoRepository;
import com.matsinger.barofishserver.domain.product.application.ProductQueryService;
import com.matsinger.barofishserver.domain.product.domain.Product;
import com.matsinger.barofishserver.domain.productinfonotice.repository.ProcessedFoodInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductInfoNotificationQueryService {

    private final ProductQueryService productQueryService;
    private final AgriculturalAndLivestockProductsInfoRepository agriculturalAndLivestockProductsInfoRepository;
    private final ProcessedFoodInfoRepository processedFoodInfoRepository;

    public ProductInformation getProductInfoNotificationForm(String itemCode) {
        return ProductInfoNoticeForm.getProductInformationForm(itemCode);
    }

    public ProductInformation getProductInfoNotification(int productId) {
        Product findProduct = productQueryService.findById(productId);

        if (findProduct.getItemCode().equals(ProductInfoNoticeForm.LIVESTOCK.getItemCode())) {
            AgriculturalAndLivestockProductsInfo findInfo = findLivestockProductsInfoByProductId(productId);
            return findInfo.toDto();
        }
        if (findProduct.getItemCode().equals(ProductInfoNoticeForm.PROCESSED.getItemCode())) {
            ProcessedFoodInfo findInfo = findProcessedFoodInfoByProductId(productId);
            return findInfo.toDto();
        }

        return null;
    }

    public ProcessedFoodInfo findProcessedFoodInfoByProductId(int productId) {
        Optional<ProcessedFoodInfo> optionalProcessedFoodInfo = processedFoodInfoRepository.findByProductId(productId);
        return optionalProcessedFoodInfo.orElse(null);
    }

    public AgriculturalAndLivestockProductsInfo findLivestockProductsInfoByProductId(int productId) {
        Optional<AgriculturalAndLivestockProductsInfo> optionalAgriculturalAndLivestockProductsInfo = agriculturalAndLivestockProductsInfoRepository.findByProductId(productId);
        return optionalAgriculturalAndLivestockProductsInfo.orElse(null);
    }
}
