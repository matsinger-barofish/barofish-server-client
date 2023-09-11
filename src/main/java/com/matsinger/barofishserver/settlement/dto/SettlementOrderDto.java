package com.matsinger.barofishserver.settlement.dto;

import lombok.*;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class SettlementOrderDto {
    private String orderId;
    private String couponName;
    private int couponDiscount;
    private int usePoint;
    @Builder.Default
    private int orderDeliveryFeeSum = 0;

    @Builder.Default
    private List<SettlementStoreDto> settlementStoreDtos = new ArrayList<>();

    public void addStoreInSameOrder(SettlementStoreDto storeDto) {
        settlementStoreDtos.add(storeDto);
    }

    public void addDeliveryFee(int storeDeliveryFeeSum) {
        this.orderDeliveryFeeSum += storeDeliveryFeeSum;
    }

    @Override
    public String toString() {
        return "SettlementOrderDto{" +
                "orderId='" + orderId + '\'' +
                ", couponName='" + couponName + '\'' +
                ", couponDiscount=" + couponDiscount +
                ", usePoint=" + usePoint +
                ", orderDeliveryFeeSum=" + orderDeliveryFeeSum +
                ", settlementStoreDtos=" + settlementStoreDtos +
                '}';
    }
}
