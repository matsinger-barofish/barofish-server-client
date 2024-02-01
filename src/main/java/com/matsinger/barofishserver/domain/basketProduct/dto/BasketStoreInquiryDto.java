package com.matsinger.barofishserver.domain.basketProduct.dto;

import com.matsinger.barofishserver.domain.store.domain.StoreDeliverFeeType;
import lombok.Getter;

import java.util.List;

@Getter
public class BasketStoreInquiryDto {

    private Integer userId;
    private Integer storeId;
    private String storeName;
    private String profileImage;

    private List<BasketProductInquiryDto> products;

    private StoreDeliverFeeType deliveryFeeType;
    private Integer totalDeliveryFee;
    private int minOrderPrice;
    private Integer totalPrice;

    public void calculateDeliveryFee() {
        for (BasketProductInquiryDto product : products) {

        }

    }
}
