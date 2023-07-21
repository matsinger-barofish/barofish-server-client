package com.matsinger.barofishserver.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResetPasswordReq {

    private String phone;
    private Integer verificationId;
}
