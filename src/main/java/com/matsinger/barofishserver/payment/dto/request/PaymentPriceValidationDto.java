package com.matsinger.barofishserver.payment.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentPriceValidationDto {

    private String merchant_uid; // 가맹점 주문번호
    private int amount; // 결제 예정금액
}
