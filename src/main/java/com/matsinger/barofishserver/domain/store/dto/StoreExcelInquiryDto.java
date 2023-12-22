package com.matsinger.barofishserver.domain.store.dto;

import com.matsinger.barofishserver.domain.store.domain.StoreState;
import lombok.Getter;

@Getter
public class StoreExcelInquiryDto {

    private String storeName;
    private String loginId;
    private StoreState state;
    private String location;
    private String deliveryCompany;
    private String settlementRate;
    private String bankName;
    private String bankHolder;
    private String bankAccount;
    private String representativeName;
    private String companyId; // 사업자 번호
    private String businessType;
    private String mosRegistrationNumber; // 통신판매신고번호
    private String businessAddress; // 사업장 주소
    private String postalCode; // 우편번호
    private String lotNumberAddress; // 지번
    private String streetNameAddress; // 도로명주소
    private String addressDetail; // 상세주소
    private String phoneNumber;
    private String email;
    private String faxNumber;
}
