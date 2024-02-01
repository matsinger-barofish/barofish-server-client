package com.matsinger.barofishserver.domain.payment.dto;

import com.matsinger.barofishserver.domain.user.paymentMethod.domain.PaymentMethod;
import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KeyInPaymentReq {
    String order_name;
    Integer total_amount;
    String orderId;
    PaymentMethod paymentMethod;
    Integer taxFree;


}