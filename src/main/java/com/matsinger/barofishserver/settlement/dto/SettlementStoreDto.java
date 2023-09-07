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

    private List<SettlementProductOptionItemDto> settlementProductOptionItemDtos;

    @Override
    public String toString() {
        return "SettlementStoreDto{" +
                "partnerName='" + partnerName + '\'' +
                ", deliveryFeeSum=" + deliveryFeeSum +
                ", totalPriceSum=" + totalPriceSum +
                ", settlementProductOptionItemDtos=" + settlementProductOptionItemDtos +
                '}';
    }
}
