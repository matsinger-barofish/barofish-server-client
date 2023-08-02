package com.matsinger.barofishserver.user.dto;

import com.matsinger.barofishserver.user.domain.UserState;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class UpdateUserStateReq {

    private List<Integer> userIds;
    private UserState state;
}
