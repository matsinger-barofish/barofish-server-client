package com.matsinger.barofishserver.domain.basketProduct.dto;

import com.matsinger.barofishserver.domain.product.domain.ProductDeliverFeeType;
import com.matsinger.barofishserver.domain.product.optionitem.dto.OptionItemDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder @AllArgsConstructor @NoArgsConstructor
public class BasketProductDtoV2 {
    Integer id;
    BasketStoreDto store;
    BasketProductInfoDto product;
    Integer amount;
    Integer inventoryQuantity;
    ProductDeliverFeeType deliverFeeType;
    Integer minOrderPrice;
    Integer deliveryFee;
    Boolean isConditional;
    Integer minStorePrice;
    OptionItemDto option;

    public void setStore(BasketStoreDto store) {
        this.store = store;
    }

    public void setProduct(BasketProductInfoDto product) {
        this.product = product;
    }

    public void setOption(OptionItemDto option) {
        this.option = option;
    }
}

