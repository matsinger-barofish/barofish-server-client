package com.matsinger.barofishserver.domain.basketProduct.dto;

import com.matsinger.barofishserver.domain.product.domain.ProductDeliverFeeType;
import com.matsinger.barofishserver.domain.product.optionitem.dto.OptionItemDto;
import com.matsinger.barofishserver.domain.product.dto.ProductListDto;
import com.matsinger.barofishserver.domain.store.dto.SimpleStore;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BasketProductDto {
    Integer id;
    SimpleStore store;
    ProductListDto product;
    Integer amount;
    ProductDeliverFeeType deliverFeeType;
    Integer minOrderPrice;
    OptionItemDto option;
    Integer deliveryFee;
    Boolean isConditional;
    Integer minStorePrice;
}
