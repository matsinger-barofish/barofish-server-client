package com.matsinger.barofishserver.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserJoinReq {

    private String email;
    private String name;
    private String nickname;
    private String password;
    private String phone;
    private Integer verificationId;
    private String impUid;
    private String bcode;
    private String postalCode;
    private String address;
    private String addressDetail;
    private Boolean isAgreeMarketing;
}
