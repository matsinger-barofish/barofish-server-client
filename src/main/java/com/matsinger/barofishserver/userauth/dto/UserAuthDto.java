package com.matsinger.barofishserver.userauth.dto;

import com.matsinger.barofishserver.userauth.domain.LoginType;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthDto {
    private LoginType loginType;
    private String loginId;
    private Integer userId;
}
