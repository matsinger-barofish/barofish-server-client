package com.matsinger.barofishserver.order.dto.request;

import com.matsinger.barofishserver.order.OrderProductOption;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class OrderRequestDto {

    private String userId;
    private int totalPrice;

    private List<OrderProductInfoDto> products;

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class OrderProductInfoDto {

        private int productId;
        private int originPrice;
        private double discountRate;
        private int amount;
        private int deliveryFee;
        private List<OrderProductOptionDto> options;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class OrderProductOptionDto {

        private int optionId;
        private String optionName;
        private int optionPrice;
    }
}
