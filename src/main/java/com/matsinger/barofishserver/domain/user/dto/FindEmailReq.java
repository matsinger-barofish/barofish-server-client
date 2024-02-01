package com.matsinger.barofishserver.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FindEmailReq {
    private String phone;
    private Integer verificationId;
}
