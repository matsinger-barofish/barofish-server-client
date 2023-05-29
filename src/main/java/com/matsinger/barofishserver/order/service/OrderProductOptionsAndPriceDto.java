package com.matsinger.barofishserver.order.service;

import com.matsinger.barofishserver.order.OrderProductOption;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public record OrderProductOptionsAndPriceDto(int optionPriceSum, List<OrderProductOption> options) {

}
