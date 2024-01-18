package com.matsinger.barofishserver.domain.store.dto;

import com.matsinger.barofishserver.domain.store.domain.StoreState;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
public class StoreDto {
    Integer id;
    StoreState state;
    String loginId;
    Timestamp joinAt;
    String backgroundImage;
    String profileImage;
    Boolean isReliable;
    String name;
    String location;
    String[] keyword;
    String visitNote;
    Integer refundDeliverFee;
    String oneLineDescription;
    String deliverCompany;
    StoreAdditionalDto additionalData;
    Boolean isConditional;
    Integer minOrderPrice;
    Integer deliveryFee;
}
