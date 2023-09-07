package com.matsinger.barofishserver.settlement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SettlementOrderDto {

    private String orderId;
    private String couponName;
    private int couponDiscount;
    private int usePoint;
    private int finalPaymentPrice;                           // 최종결제금액(원) 총 금액 - 쿠폰할인 - 포인트    개별 | 파트너끝 | 주문끝

    private List<SettlementStoreDto> settlementStoreDtos;

    @Override
    public String toString() {
        return "SettlementOrderDto{" +
                "orderId='" + orderId + '\'' +
                ", couponName='" + couponName + '\'' +
                ", couponDiscount=" + couponDiscount +
                ", usePoint=" + usePoint +
                ", finalPaymentPrice=" + finalPaymentPrice +
                ", settlementStoreDtos=" + settlementStoreDtos +
                '}';
    }
}
