package com.matsinger.barofishserver.domain.order.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GetCancelPriceDto {
    int cancelPrice;
    int returnPoint;
}
