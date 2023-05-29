package com.matsinger.barofishserver.order.dto.response;

import com.matsinger.barofishserver.order.dto.response.OrderProductInfoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStoreInfoDto {

    private int storeId;
    private String storeName;
    private int price;
    private List<OrderProductInfoDto> storeProducts;
}
