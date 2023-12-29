package com.matsinger.barofishserver.domain.banner.application;

import com.matsinger.barofishserver.domain.banner.domain.Banner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest
@ActiveProfiles("local")
class BannerQueryServiceTest {

    @Autowired private BannerQueryService bannerQueryService;

    @DisplayName("")
    @Test
    void test() {
        // given
        List<Banner> banners = bannerQueryService.selectBannersOrderBySortNum();
        // when

        // then
    }
}