package com.matsinger.barofishserver.order.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class OrderReqProductOptionDto {

    private int optionId;
    private int amount;
}
