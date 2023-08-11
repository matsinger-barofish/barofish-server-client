package com.matsinger.barofishserver.order.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class OrderProductReq {
    Integer productId;
    Integer optionItemId;
    Integer amount;
    Boolean needTaxation;
    Integer deliveryFee;
}
