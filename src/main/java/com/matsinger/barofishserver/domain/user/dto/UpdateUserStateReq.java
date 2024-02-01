package com.matsinger.barofishserver.domain.user.dto;

import com.matsinger.barofishserver.domain.user.domain.UserState;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class UpdateUserStateReq {

    private List<Integer> userIds;
    private UserState state;
}
