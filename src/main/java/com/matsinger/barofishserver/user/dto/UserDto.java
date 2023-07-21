package com.matsinger.barofishserver.user.dto;

import com.matsinger.barofishserver.user.domain.UserState;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private int id;
    private UserState state;
    private Timestamp joinAt;

}
