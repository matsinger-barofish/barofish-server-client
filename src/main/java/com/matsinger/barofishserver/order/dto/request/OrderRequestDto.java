package com.matsinger.barofishserver.order.dto.request;

import com.matsinger.barofishserver.order.dto.OrderProductInfoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class OrderRequestDto {

    private String loginId;
    private int totalPrice;
    private String name;

    private List<OrderProductInfoDto> products;
}
