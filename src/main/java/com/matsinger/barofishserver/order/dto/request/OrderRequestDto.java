package com.example.demo.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class OrderRequestDto {

    private int userId;
    private int totalPrice;
    private int couponId;
    private int usePoint;

    private List<OrderProductInfoDto> orderProductInfos;

    public static class OrderProductInfoDto {

        private List<OrderProductOptionDto> orderProductOptions;
    }

    public static class OrderProductOptionDto {

    }
}
