package com.matsinger.barofishserver.basketProduct.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AddBasketOptionReq {
    private Integer optionId;
    private Integer amount;
}
