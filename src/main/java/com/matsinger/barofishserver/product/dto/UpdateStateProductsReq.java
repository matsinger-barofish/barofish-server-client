package com.matsinger.barofishserver.product.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class UpdateStateProductsReq {

    private List<Integer> productIds;
    private Boolean isActive;
}
