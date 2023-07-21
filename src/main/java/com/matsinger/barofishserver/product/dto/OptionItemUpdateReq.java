package com.matsinger.barofishserver.product.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OptionItemUpdateReq {

    private Boolean isRepresent;
    private String name;
    private Integer discountPrice;
    private Integer amount;
    private Integer purchasePrice;
    private Integer originPrice;
    private Integer deliveryFee;
    private Integer deliverBoxPerAmount;
    private Integer maxAvailableAmount;
}
