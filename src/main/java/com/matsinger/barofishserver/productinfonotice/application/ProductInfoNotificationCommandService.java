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
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.matsinger.barofishserver.productinfonotice.domain.ProductInfoNoticeManager.LIVESTOCK;
import static com.matsinger.barofishserver.productinfonotice.domain.ProductInfoNoticeManager.PROCESSED;

@Service
@RequiredArgsConstructor
public class ProductInfoNotificationCommandService {
    private final ProductQueryService productQueryService;
    private final AgriculturalAndLivestockProductsInfoRepository agriculturalAndLivestockProductsInfoRepository;
    private final ProcessedFoodInfoRepository processedFoodInfoRepository;

    @Transactional
    public void addProductInfoNotification(ProductInformation request) {
        Product findProduct = productQueryService.findById(request.getProductId());

        Optional<String> optionalItemCode = Optional.ofNullable(findProduct.getItemCode());
        if (optionalItemCode.isPresent() && !optionalItemCode.get().equals(request.getItemCode())) {
            throw new IllegalArgumentException("삼품의 품목 아이디가 일치하지 않습니다.");
        }

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
}
