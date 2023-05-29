package com.matsinger.barofishserver.order.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class OrderReqProductInfoDto {

    private int productId;
    private int storeId;
    private List<OrderReqProductOptionDto> options;
}
