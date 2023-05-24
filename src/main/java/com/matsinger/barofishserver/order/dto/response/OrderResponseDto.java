package com.matsinger.barofishserver.order.dto.response;

import com.matsinger.barofishserver.order.dto.OrderProductInfoDto;
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

    private String userId;
    private int totalPrice;

    private List<OrderProductInfoDto> products;
}
