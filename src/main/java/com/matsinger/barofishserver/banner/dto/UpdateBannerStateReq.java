package com.matsinger.barofishserver.banner.dto;

import com.matsinger.barofishserver.banner.domain.BannerState;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class UpdateBannerStateReq {
    List<Integer> ids;
    BannerState state;
}
