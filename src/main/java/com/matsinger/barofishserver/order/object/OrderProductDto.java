package com.matsinger.barofishserver.order.object;

import com.matsinger.barofishserver.product.object.ProductListDto;
import com.matsinger.barofishserver.product.object.SimpleProductDto;
import com.matsinger.barofishserver.store.object.SimpleStore;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OrderProductDto {
    private ProductListDto product;
    private OrderProductState state;
    private Integer price;
    private Integer amount;
    private Integer deliveryFee;
}
