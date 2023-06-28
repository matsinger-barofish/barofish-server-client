package com.matsinger.barofishserver.main;

import com.matsinger.barofishserver.banner.Banner;
import com.matsinger.barofishserver.data.curation.object.CurationDto;
import com.matsinger.barofishserver.data.tip.Tip;
import com.matsinger.barofishserver.data.topbar.TopBar;
import com.matsinger.barofishserver.store.object.SimpleStore;
import com.matsinger.barofishserver.store.object.StoreInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Main {
    List<TopBar> topBars;
    List<Banner> banners;
    List<CurationDto> curations;
    List<Banner> subBanner;
    SimpleStore store;
}
