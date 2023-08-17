package com.matsinger.barofishserver.productinfonotice.application;

import com.matsinger.barofishserver.product.application.ProductQueryService;
import com.matsinger.barofishserver.product.domain.Product;
import com.matsinger.barofishserver.product.option.domain.Option;
import com.matsinger.barofishserver.productinfonotice.domain.AgriculturalAndLivestockProductsInfo;
import com.matsinger.barofishserver.productinfonotice.domain.ProcessedFoodInfo;
import com.matsinger.barofishserver.productinfonotice.domain.ProductInformation;
import com.matsinger.barofishserver.productinfonotice.dto.AgriculturalAndLivestockProductsInfoDto;
import com.matsinger.barofishserver.productinfonotice.dto.ProcessedFoodInfoDto;
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
    private final ProductInfoNotificationQueryService productInfoNotificationQueryService;

    @Transactional
    public void addProductInfoNotification(ProductInformation request) {
        Product findProduct = productQueryService.findById(request.getProductId());

        Optional<String> optionalItemCode = Optional.ofNullable(findProduct.getItemCode());
        if (optionalItemCode.isPresent() && !optionalItemCode.get().equals(request.getItemCode())) {
            throw new IllegalArgumentException("상품의 품목 아이디가 일치하지 않습니다.");
        }

        if (optionalItemCode.isPresent()) {
            throw new IllegalArgumentException("상품 고시 정보가 이미 존재합니다.");
        }

        if (optionalItemCode.isEmpty()) {
            findProduct.setItemCode(request.getItemCode());
            if (request.getItemCode().equals(LIVESTOCK.getItemCode())) {
                agriculturalAndLivestockProductsInfoRepository.save((AgriculturalAndLivestockProductsInfo) request.toEntity(findProduct));
                return;
            }
            if (request.getItemCode().equals(PROCESSED.getItemCode())) {
                processedFoodInfoRepository.save((ProcessedFoodInfo) request.toEntity(findProduct));
                return;
            }
        }

        throw new IllegalArgumentException("올바르지 않은 품목 코드입니다.");
    }

    @Transactional
    public void updateProductInfoNotification(ProductInformation request) {
        Product findProduct = productQueryService.findById(request.getProductId());

        Optional<String> optionalItemCode = Optional.ofNullable(findProduct.getItemCode());

        if (optionalItemCode.isEmpty()) {
            throw new IllegalArgumentException("상품의 품목 정보가 존재하지 않습니다.");
        }
        if (optionalItemCode.isPresent() && !optionalItemCode.get().equals(request.getItemCode())) {
            throw new IllegalArgumentException("상품의 품목 아이디가 일치하지 않습니다.");
        }

        if (request.getItemCode().equals(LIVESTOCK.getItemCode())) {
            AgriculturalAndLivestockProductsInfo findInfo = productInfoNotificationQueryService.findLivestockProductsInfoByProductId(findProduct.getId());
            findInfo.update((AgriculturalAndLivestockProductsInfoDto) request);
            return;
        }
        if (request.getItemCode().equals(PROCESSED.getItemCode())) {
            ProcessedFoodInfo findInfo = productInfoNotificationQueryService.findProcessedFoodInfoByProductId(findProduct.getId());
            findInfo.update((ProcessedFoodInfoDto) request);
            return;
        }
    }

    @Transactional
    public void deleteProductInfoNotification(int productId) {
        Product findProduct = productQueryService.findById(productId);

        if (findProduct.getItemCode().equals(LIVESTOCK.getItemCode())) {
            agriculturalAndLivestockProductsInfoRepository.deleteByProductId(productId);
            findProduct.setItemCode(null);
            return;
        }
        if (findProduct.getItemCode().equals(PROCESSED.getItemCode())) {
            processedFoodInfoRepository.deleteByProductId(productId);
            findProduct.setItemCode(null);
            return;
        }

        throw new IllegalArgumentException("상품 고시 정보를 찾을 수 없습니다.");
    }
}
