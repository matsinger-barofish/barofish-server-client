package com.matsinger.barofishserver.productinfonotice.domain;

public enum ProductInfoNoticeManager {

    LIVESTOCK("19", AgriculturalAndLivestockProductsInfo.getForm()),
    PROCESSED("20", ProcessedFoodInfo.getForm());

    private String itemCode;
    private ProductInformation productInformation;

    ProductInfoNoticeManager(String itemCode, ProductInformation productInformation) {
        this.itemCode = itemCode;
        this.productInformation = productInformation;
    }


}
