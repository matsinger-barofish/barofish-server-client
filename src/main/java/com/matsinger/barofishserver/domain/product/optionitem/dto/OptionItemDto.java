package com.matsinger.barofishserver.domain.product.optionitem.dto;

import lombok.*;

@Getter
@Setter
@Builder @AllArgsConstructor @NoArgsConstructor
public class OptionItemDto {
    private Integer id;
    private Integer optionId;
    private String name;
    private Integer discountPrice;
    private Integer amount;
    private Integer inventoryQuantity;
    private Integer purchasePrice;
    private Integer originPrice;
    private Integer deliveryFee;
    private Integer deliverBoxPerAmount;
    private Integer maxAvailableAmount;
    private Float pointRate;
    private Integer minOrderPrice;
}
