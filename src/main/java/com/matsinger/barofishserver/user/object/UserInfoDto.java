package com.matsinger.barofishserver.user.object;

import com.matsinger.barofishserver.grade.Grade;
import com.matsinger.barofishserver.userauth.UserAuthDto;
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
}
