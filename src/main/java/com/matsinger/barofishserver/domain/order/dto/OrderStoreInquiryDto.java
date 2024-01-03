package com.matsinger.barofishserver.domain.order.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class OrderStoreInquiryDto {

    private List<OrderProductInquiryDto> products;
}
