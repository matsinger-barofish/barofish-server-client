package com.matsinger.barofishserver.order.dto;

import com.matsinger.barofishserver.order.OrderState;
import com.matsinger.barofishserver.order.dto.request.OrderRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class OrderProductInfoDto {

    private int productId;
    private int originPrice;
    private double discountRate;
    private int amount;
    private int deliveryFee;
    private OrderState state;
    private List<OrderProductOptionDto> options;
}
