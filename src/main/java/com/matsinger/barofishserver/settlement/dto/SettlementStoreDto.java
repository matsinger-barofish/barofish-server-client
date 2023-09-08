package com.matsinger.barofishserver.settlement.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder @AllArgsConstructor @NoArgsConstructor
public class SettlementStoreDto {

    private int storeId;                                    // 파트너 아이디
    private String partnerName;                             // 파트너
    private int storeDeliveryFeeSum = 0;                        // 배송비 합                               개별 | 파트너끝
    private int storeTotalPriceSum = 0;                         // 총 금액(원) (판매가 * 수량) - 배송비         개별 | 파트너끝

    List<SettlementProductOptionItemDto> storeOptionItemDtos = new ArrayList<>();

    public void addDeliveryFee(int deliveryFee) {
        this.storeDeliveryFeeSum += deliveryFee;
    }

    public void addPrice(int price) {
        this.storeTotalPriceSum += price;
    }

    @Override
    public String toString() {
        return "SettlementStoreDto{" +
                "storeId=" + storeId +
                ", partnerName='" + partnerName + '\'' +
                ", storeDeliveryFeeSum=" + storeDeliveryFeeSum +
                ", storeTotalPriceSum=" + storeTotalPriceSum +
                ", storeOptionItemDtos=" + storeOptionItemDtos +
                '}';
    }
}
