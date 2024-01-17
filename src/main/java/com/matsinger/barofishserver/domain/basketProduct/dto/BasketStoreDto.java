package com.matsinger.barofishserver.domain.basketProduct.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder @AllArgsConstructor @NoArgsConstructor
public class BasketStoreDto {
    private Integer storeId;
    private String name;
    private String backgroundImage;
    private String profileImage;
    private Boolean isConditional;
    private Integer minStorePrice;
    private Integer deliveryFee;
}
