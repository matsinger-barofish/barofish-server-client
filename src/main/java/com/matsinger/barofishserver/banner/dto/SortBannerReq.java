package com.matsinger.barofishserver.banner.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class SortBannerReq {
    List<Integer> bannerIds;
}
