package com.matsinger.barofishserver.domain.admin.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AddAdminReq {
    String loginId;
    String password;
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