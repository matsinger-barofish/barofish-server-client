package com.matsinger.barofishserver.domain.main.dto;

import com.matsinger.barofishserver.domain.banner.domain.Banner;
import com.matsinger.barofishserver.domain.data.topbar.domain.TopBar;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Main {
    List<TopBar> topBars;
    List<Banner> banners;
    List<Banner> subBanner;
}
