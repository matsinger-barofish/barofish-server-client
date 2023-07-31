package com.matsinger.barofishserver.basketProduct.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class AddBasketReq {
    private Integer productId;
    private List<AddBasketOptionReq> options;
}