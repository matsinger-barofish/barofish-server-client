package com.matsinger.barofishserver.user.object;

import com.matsinger.barofishserver.userauth.UserAuth;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private User user;
    private UserAuth auth;
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
}
