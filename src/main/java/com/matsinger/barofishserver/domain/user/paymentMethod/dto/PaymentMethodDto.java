package com.matsinger.barofishserver.domain.user.paymentMethod.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentMethodDto {
    private int id;
    private int userId;
    private String name;
    private String cardName;
    private String cardNo;
    private String expiryAt;
    private String birth;
}
