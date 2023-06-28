package com.matsinger.barofishserver.userauth;

import jakarta.persistence.*;
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
