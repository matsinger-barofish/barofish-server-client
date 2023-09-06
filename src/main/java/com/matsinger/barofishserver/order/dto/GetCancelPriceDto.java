package com.matsinger.barofishserver.order.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GetCancelPriceDto {
    int cancelPrice;
    int returnPoint;
}
