package com.matsinger.barofishserver.store.object;

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
    String name;
    String location;
    String[] keyword;
    String visitNote;
}
