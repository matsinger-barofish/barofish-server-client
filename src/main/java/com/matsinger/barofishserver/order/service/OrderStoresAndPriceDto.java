package com.matsinger.barofishserver.order.service;

import com.matsinger.barofishserver.order.OrderStoreInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class OrderStoresAndPriceDto {
    private final int storePriceSum;
    private final List<OrderStoreInfo> stores;
}
