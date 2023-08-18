package com.matsinger.barofishserver.product.dto;

import lombok.*;

import java.util.List;

@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ExcelProductDto {
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
    List<Integer> purchasePrices;
    Integer representativeOptionNo;
    List<String> optionNames;
    List<Integer> optionOriginPrices;
    List<Integer> optionDiscountPrices;
    List<Integer> optionMaxOrderAmount;
    List<Integer> optionAmounts;
    Float pointRate;
}
