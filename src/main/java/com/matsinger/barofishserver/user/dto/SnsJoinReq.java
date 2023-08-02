package com.matsinger.barofishserver.user.dto;

import com.matsinger.barofishserver.userauth.domain.LoginType;
import com.matsinger.barofishserver.userauth.domain.UserAuth;
import com.matsinger.barofishserver.userinfo.domain.UserInfo;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SnsJoinReq {

    private LoginType loginType;
    private String loginId;
    private String profileImage;
    private String email;
    private String name;
    private String nickname;
    private String phone;

    public UserInfo toUserInfo(String phoneNumber) {
        return UserInfo.builder()
                .nickname(nickname!=null ? nickname : "")
                .email(email!=null ? email : "")
                .name(name!=null ? name : "")
                .phone(phoneNumber)
                .point(0)
                .build();
    }

    public UserAuth toUserAuth() {
        return UserAuth.builder()
                .loginId(this.loginId)
                .loginType(this.loginType)
                .build();
    }
}
