package com.matsinger.barofishserver.order.service.dto;

import com.matsinger.barofishserver.order.OrderProductInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class OrderProductsAndPriceDto {

    private int getProductPriceSum;
    private List<OrderProductInfo> products;
}
