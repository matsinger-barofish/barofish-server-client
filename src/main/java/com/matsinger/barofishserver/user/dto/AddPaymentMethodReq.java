package com.matsinger.barofishserver.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AddPaymentMethodReq {

    private String name;
    private String cardNo;
    private String expiryAt;
    private String birth;
    private String passwordTwoDigit;
}

