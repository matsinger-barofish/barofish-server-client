package com.matsinger.barofishserver.main.dto;

import com.matsinger.barofishserver.banner.domain.Banner;
import com.matsinger.barofishserver.data.curation.dto.CurationDto;
import com.matsinger.barofishserver.data.topbar.domain.TopBar;
import com.matsinger.barofishserver.store.dto.SimpleStore;
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
