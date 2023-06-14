package com.matsinger.barofishserver.basketProduct.obejct;

import com.matsinger.barofishserver.product.object.OptionItem;
import com.matsinger.barofishserver.product.object.OptionItemDto;
import com.matsinger.barofishserver.product.object.ProductListDto;
import com.matsinger.barofishserver.store.object.SimpleStore;
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
