package com.matsinger.barofishserver.store.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StoreAdditionalDto {
    Integer settlementRate;
    String bankName;
    String bankHolder;
    String bankAccount;
    String representativeName;
    String companyId;
    String businessType;
    String mosRegistrationNumber;
    String businessAddress;
    String postalCode;
    String lotNumberAddress;
    String streetNameAddress;
    String addressDetail;
    String tel;
    String email;
    String faxNumber;
    String mosRegistration;
    String businessRegistration;
    String bankAccountCopy;
}
