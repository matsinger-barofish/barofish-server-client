package com.matsinger.barofishserver.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder @AllArgsConstructor @NoArgsConstructor
public class ExpectedArrivalDateResponse {

    private int productExpectedArrivalDate;
    private int calculatedExpectedArrivalDate;
}
