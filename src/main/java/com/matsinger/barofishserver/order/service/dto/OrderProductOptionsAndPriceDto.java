package com.matsinger.barofishserver.order.service.dto;

import com.matsinger.barofishserver.order.OrderProductOption;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;


@Getter
@AllArgsConstructor
public class OrderProductOptionsAndPriceDto {

    private int optionPriceSum;
    private List<OrderProductOption> options;
}
