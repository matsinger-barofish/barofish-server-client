package com.matsinger.barofishserver.order.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {

    private int userId;
    private String orderId;
    private int totalPrice;

    private List<OrderStoreInfoDto> stores;
}
