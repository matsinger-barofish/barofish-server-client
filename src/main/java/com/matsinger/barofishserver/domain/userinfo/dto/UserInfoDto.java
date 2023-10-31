package com.matsinger.barofishserver.domain.userinfo.dto;

import com.matsinger.barofishserver.domain.grade.domain.Grade;
import com.matsinger.barofishserver.domain.user.deliverplace.DeliverPlace;
import com.matsinger.barofishserver.domain.user.dto.UserDto;
import com.matsinger.barofishserver.domain.userauth.dto.UserAuthDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDto {
    private UserDto user;
    private UserAuthDto auth;
    private Integer userId;
    private String profileImage;
    private String email;
    private String name;
    private String nickname;
    private String phone;
    private Grade grade;
    private Integer point;
    private Boolean isAgreeMarketing;
    private List<DeliverPlace> deliverPlaces;
    private Integer reviewCount;
    private Integer notificationCount;
    private Integer saveProductCount;
}
