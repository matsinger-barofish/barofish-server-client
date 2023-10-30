package com.matsinger.barofishserver.productinfonotice.application;

import com.matsinger.barofishserver.product.application.ProductQueryService;
import com.matsinger.barofishserver.product.domain.Product;
import com.matsinger.barofishserver.productinfonotice.domain.AgriculturalAndLivestockProductsInfo;
import com.matsinger.barofishserver.productinfonotice.domain.ProcessedFoodInfo;
import com.matsinger.barofishserver.productinfonotice.domain.ProductInformation;
import com.matsinger.barofishserver.productinfonotice.repository.AgriculturalAndLivestockProductsInfoRepository;
import com.matsinger.barofishserver.productinfonotice.repository.ProcessedFoodInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.matsinger.barofishserver.productinfonotice.domain.ProductInfoNoticeForm.*;

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
