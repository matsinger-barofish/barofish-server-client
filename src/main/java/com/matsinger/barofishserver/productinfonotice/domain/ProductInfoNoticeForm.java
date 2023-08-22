package com.matsinger.barofishserver.productinfonotice.domain;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ProductInfoNoticeForm {

    //    LIVESTOCK("19", new AgriculturalAndLivestockProductsInfoDto()),
//    PROCESSED("20", new ProcessedFoodInfoDto());
    LIVESTOCK("19", AgriculturalAndLivestockProductsInfo.getForm()),
    PROCESSED("20", ProcessedFoodInfo.getForm());

    private final String itemCode;
    private final ProductInformation productInfoForm;

    private ProductInfoNoticeForm(String itemCode, ProductInformation productInfoForm) {
        this.itemCode = itemCode;
        this.productInfoForm = productInfoForm;
    }

    public static ProductInformation getProductInformationForm(String productCode) {
        ProductInfoNoticeForm productInfoNoticeManager = Arrays.stream(ProductInfoNoticeForm.values())
                .filter(productInformation -> productInformation.getItemCode().equals(productCode))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("상품 고시 정보를 찾을 수 없습니다."));

        return productInfoNoticeManager.getProductInfoForm();
    }
}
