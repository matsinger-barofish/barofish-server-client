package com.matsinger.barofishserver.basketProduct.dto;

import com.matsinger.barofishserver.product.optionitem.dto.OptionItemDto;
import com.matsinger.barofishserver.product.dto.ProductListDto;
import com.matsinger.barofishserver.store.dto.SimpleStore;
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
    Integer deliveryFee;
    OptionItemDto option;
}
