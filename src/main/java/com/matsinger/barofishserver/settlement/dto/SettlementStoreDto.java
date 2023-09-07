package com.matsinger.barofishserver.settlement.dto;

import com.matsinger.barofishserver.order.orderprductinfo.domain.OrderProductState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SettlementStoreDto {
    private int storeId;                                    // 파트너 아이디
    private String partnerName;                             // 파트너
    private int deliveryFeeSum;                             // 배송비 합                               개별 | 파트너끝
    private int totalPriceSum;                              // 총 금액(원) (판매가 * 수량) - 배송비         개별 | 파트너끝
    private int totalOrderPrice;                            // 총 주문금액                                           | 주문끝
    private String couponName;                              // 쿠폰명                                               | 주문끝
    private int couponDiscount;                             // 쿠폰할인                                              | 주문끝
    private int usePoint;                                   // 포인트                                               | 주문끝
    private int finalPaymentPrice;                           // 최종결제금액(원) 총 금액 - 쿠폰할인 - 포인트    개별 | 파트너끝 | 주문끝

//    private List<SettlementProductOptionItemDto> productOptionItemDtos;

    @Override
    public String toString() {
        return "SettlementStoreDto{" +
                "partnerName='" + partnerName + '\'' +
                ", deliveryFeeSum=" + deliveryFeeSum +
                ", totalPriceSum=" + totalPriceSum +
                ", totalOrderPrice=" + totalOrderPrice +
                ", couponName='" + couponName + '\'' +
                ", couponDiscount=" + couponDiscount +
                ", usePoint=" + usePoint +
                ", finalPaymentPrice=" + finalPaymentPrice +
//                ", productOptionItemDtos=" + productOptionItemDtos +
                '}';
    }
}
