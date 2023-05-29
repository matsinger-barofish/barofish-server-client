package com.matsinger.barofishserver.order.service;

import com.matsinger.barofishserver.order.OrderProductInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public record OrderProductsAndPriceDto(int productPriceSum, List<OrderProductInfo> products) {

}
