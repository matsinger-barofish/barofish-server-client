package com.matsinger.barofishserver.domain.user.dto;

import com.matsinger.barofishserver.domain.grade.domain.Grade;
import com.matsinger.barofishserver.domain.user.deliverplace.DeliverPlace;
import com.matsinger.barofishserver.domain.user.domain.User;
import com.matsinger.barofishserver.domain.user.domain.UserState;
import com.matsinger.barofishserver.domain.userauth.domain.LoginType;
import com.matsinger.barofishserver.domain.userauth.domain.UserAuth;
import com.matsinger.barofishserver.domain.userinfo.domain.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

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

    public User toUserEntity() {
        return User.builder()
                   .state(UserState.ACTIVE)
                   .joinAt(Timestamp.valueOf(LocalDateTime.now()))
                   .build();
    }

    public UserAuth toUserAuthEntity(User user) {
        UserAuth createdUserAuth = UserAuth.builder()
                                           .loginType(LoginType.APPLE)
                                           .loginId(this.loginId)
                                           .userId(user.getId())
                                           .build();
        createdUserAuth.setUser(user);

        return createdUserAuth;
    }

    public UserInfo toUserInfoEntity(User user, String imageUrl, String phoneNumber, Integer point, Grade grade) {
        UserInfo createdUserInfo = UserInfo.builder()
                                           .userId(user.getId())
                                           .profileImage(imageUrl)
                                           .email("")
                                           .name(this.name)
                                           .nickname(this.nickname)
                                           .phone(phoneNumber)
                                           .isAgreeMarketing(this.isAgreeMarketing)
                                           .point(point)
                                           .grade(grade)
                                           .build();
        createdUserInfo.setUser(user);

        return createdUserInfo;
    }

    public DeliverPlace toDeliveryPlaceEntity(User user, String phoneNumber) {
        return DeliverPlace.builder()
                           .userId(user.getId())
                           .name(this.name)
                           .receiverName(this.name)
                           .tel(phoneNumber)
                           .postalCode(this.postalCode)
                           .address(this.address)
                           .addressDetail(this.addressDetail)
                           .bcode(this.bcode)
                           .deliverMessage("")
                           .isDefault(false)
                           .build();
    }
}
