package com.matsinger.barofishserver.store.dto;

import com.matsinger.barofishserver.store.domain.StoreDeliverFeeType;
import com.matsinger.barofishserver.store.domain.StoreState;
import com.matsinger.barofishserver.store.dto.StoreAdditionalDto;
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
    StoreDeliverFeeType deliverFeeType;
    Integer deliverFee;
    Integer minOrderPrice;
    String oneLineDescription;
    StoreAdditionalDto additionalData;
}
