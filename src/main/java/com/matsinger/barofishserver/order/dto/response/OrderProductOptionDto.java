package com.matsinger.barofishserver.order.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class OrderProductOptionDto {

    private int optionId;
    private String optionName;
    private int optionPrice;
    private int amount;
}
