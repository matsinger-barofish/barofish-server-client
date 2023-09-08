package com.matsinger.barofishserver.settlement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SettlementOrderRawDto {
    private String orderId;
    private String couponName;
    private int couponDiscount;
    private int usePoint;
    private int orderDeliveryFeeSum;

    private int storeId;                                    // 파트너 아이디
    private String partnerName;                             // 파트너
    private int storeDeliveryFeeSum;                        // 배송비 합                               개별 | 파트너끝
    private int storeTotalPriceSum;                         // 총 금액(원) (판매가 * 수량) - 배송비         개별 | 파트너끝

    private List<SettlementProductOptionItemDto> settlementProductOptionItemDtos;

    public void setStoreDeliveryFeeSum(int storeDeliveryFeeSum) {
        this.storeDeliveryFeeSum = storeDeliveryFeeSum;
    }

    public void setStoreTotalPriceSum(int storeTotalPriceSum) {
        this.storeTotalPriceSum = storeTotalPriceSum;
    }

    @Override
    public String toString() {
        return "SettlementOrderAndStoreDto{" +
                "orderId='" + orderId + '\'' +
                ", couponName='" + couponName + '\'' +
                ", couponDiscount=" + couponDiscount +
                ", usePoint=" + usePoint +
                ", orderDeliveryFeeSum=" + orderDeliveryFeeSum +
                ", storeId=" + storeId +
                ", partnerName='" + partnerName + '\'' +
                ", storeDeliveryFeeSum=" + storeDeliveryFeeSum +
                ", storeTotalPriceSum=" + storeTotalPriceSum +
                ", settlementProductOptionItemDtos=" + settlementProductOptionItemDtos +
                '}';
    }
}
