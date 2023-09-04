package com.matsinger.barofishserver.user.dto;

import com.matsinger.barofishserver.userauth.domain.LoginType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AppleJoinReq {
    private String loginId;
    private String name;
    private String nickname;
    private String phone;
    private Integer verificationId;
    private String bcode;
    private String postalCode;
    private String address;
    private String addressDetail;
    private Boolean isAgreeMarketing;
}
