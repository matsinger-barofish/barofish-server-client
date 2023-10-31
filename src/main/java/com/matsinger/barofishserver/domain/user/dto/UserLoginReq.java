package com.matsinger.barofishserver.domain.user.dto;

import com.matsinger.barofishserver.domain.userauth.domain.LoginType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginReq {

    private LoginType loginType;
    private String loginId;
    private String password;
}
