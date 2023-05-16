package com.matsinger.barofishserver.main;

import com.matsinger.barofishserver.banner.Banner;
import com.matsinger.barofishserver.banner.BannerService;
import com.matsinger.barofishserver.data.*;
import com.matsinger.barofishserver.utils.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/main")
public class MainController {

    private final TopBarService topBarService;
    private final BannerService bannerService;
    private final CurationService curationService;
    private final TipService tipService;
    //TODO: 스토어 정보

    @GetMapping("")
    public ResponseEntity<CustomResponse> selectMainItems() {
        CustomResponse res = new CustomResponse();
        try {
            Main data = new Main();
            List<TopBar> topBars = topBarService.selectTopBarList();
            List<Banner> banners = bannerService.selectBannerList();
            List<Curation> curations = curationService.selectCurations();
            List<Tip> tips = tipService.selectTipList();
            data.setTips(tips);
            data.setCurations(curations);
            data.setBanners(banners);
            data.setTopBars(topBars);
            res.setData(Optional.of(data));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            res.setIsSuccess(false);
            res.setErrorMsg(e.getMessage());
            return ResponseEntity.ok(res);
        }
    }
}
