package com.matsinger.barofishserver.domain.admin.dto;

import com.matsinger.barofishserver.domain.admin.domain.AdminState;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateAdminReq {
    String password;
    AdminState state;
    String name;
    String tel;
    Boolean accessUser;
    Boolean accessProduct;
    Boolean accessOrder;
    Boolean accessSettlement;
    Boolean accessBoard;
    Boolean accessPromotion;
    Boolean accessSetting;
}
