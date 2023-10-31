package com.matsinger.barofishserver.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExcelProductDto2 {

    String storeLoginId;
    String storeName;
    String firstCategoryName;
    String secondCategoryName;
    String productName;
    Integer expectedDeliverDay;
    String deliveryInfo;
    Integer deliveryFee;
    Integer deliverBoxPerAmount;
    String isActive;
    String needTaxation;
    String hasOption;
    Integer purchasePrices;
    Integer representativeOptionNo;
    String optionName;
    Integer optionOriginPrice;
    Integer optionDiscountPrice;
    Integer optionMaxOrderAmount;
    Integer optionAmount;
    Float pointRate;
}
