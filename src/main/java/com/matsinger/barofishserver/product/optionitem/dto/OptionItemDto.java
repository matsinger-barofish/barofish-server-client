package com.matsinger.barofishserver.product.optionitem.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OptionItemDto {
    Integer id;
    Integer optionId;
    String name;
    Integer discountPrice;
    Integer amount;
    Integer purchasePrice;
    Integer originPrice;
    Integer deliveryFee;
    Integer deliverBoxPerAmount;
    Integer maxAvailableAmount;

}