package com.matsinger.barofishserver.order.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProcessDeliverStartReq {
    private String deliverCompanyCode;
    private String invoice;
}