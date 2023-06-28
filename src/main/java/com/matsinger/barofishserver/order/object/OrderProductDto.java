package com.matsinger.barofishserver.order.object;

import com.matsinger.barofishserver.product.object.OptionItemDto;
import com.matsinger.barofishserver.product.object.ProductListDto;
import com.matsinger.barofishserver.product.object.SimpleProductDto;
import com.matsinger.barofishserver.store.object.SimpleStore;
import com.matsinger.barofishserver.store.object.StoreDeliverFeeType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OrderProductDto {
    private Integer id;
    private Integer storeId;
    private String storeProfile;
    private String storeName;
    private Integer deliverFee;
    private StoreDeliverFeeType deliverFeeType;
    private Integer minOrderPrice;
    private ProductListDto product;
    private OrderProductState state;
    private String optionName;
    private OptionItemDto optionItem;
    private Integer price;
    private Integer amount;
    //    private Integer deliveryFee;
    private Boolean isReviewWritten;
}
