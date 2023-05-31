package com.matsinger.barofishserver.payment.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortOneWebhookReqDto {

    private String imp_uid; // 결제번호
    private String merchant_uid; // 주문번호
    private String status; // 결제 결과
}
