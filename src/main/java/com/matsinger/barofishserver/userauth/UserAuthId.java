package com.matsinger.barofishserver.userauth;


import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(UserAuthId.class)
@Table(name = "user_auth")
public class UserAuthId implements Serializable {

    private LoginType loginType;
    private String loginId;
}
