package com.matsinger.barofishserver.main;

import com.matsinger.barofishserver.banner.Banner;
import com.matsinger.barofishserver.data.Curation;
import com.matsinger.barofishserver.data.Tip;
import com.matsinger.barofishserver.data.TopBar;
import com.matsinger.barofishserver.store.Store;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Main {
    List<TopBar> topBars;
    List<Banner> banners;
    List<Curation> curations;
    Store store;
    List<Tip> tips;
}
