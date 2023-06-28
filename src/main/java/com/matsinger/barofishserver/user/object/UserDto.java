package com.matsinger.barofishserver.user.object;

import jakarta.persistence.*;
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
