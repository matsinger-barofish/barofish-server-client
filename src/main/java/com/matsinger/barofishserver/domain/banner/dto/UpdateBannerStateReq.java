package com.matsinger.barofishserver.domain.banner.dto;

import com.matsinger.barofishserver.domain.banner.domain.BannerState;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class UpdateBannerStateReq {
    List<Integer> ids;
    BannerState state;
}
