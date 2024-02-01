package com.matsinger.barofishserver.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateReq {

    private String name;
    private String nickname;
    private String oldPassword;
    private String newPassword;
    private String phone;
    private Integer verificationId;
    private String address;
    private String addressDetail;
    private Boolean isAgreeMarketing;
}
