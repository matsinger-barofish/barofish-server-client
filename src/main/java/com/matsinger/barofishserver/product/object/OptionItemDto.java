package com.matsinger.barofishserver.product.object;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OptionItemDto {
    Integer id;
    Integer optionId;
    String name;
    Integer discountRate;
    Integer price;
    Integer amount;
}
