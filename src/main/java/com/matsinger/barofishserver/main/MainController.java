package com.matsinger.barofishserver.main;

import com.matsinger.barofishserver.banner.Banner;
import com.matsinger.barofishserver.banner.BannerService;
import com.matsinger.barofishserver.data.curation.object.Curation;
import com.matsinger.barofishserver.data.curation.object.CurationDto;
import com.matsinger.barofishserver.data.curation.CurationService;
import com.matsinger.barofishserver.data.tip.Tip;
import com.matsinger.barofishserver.data.tip.TipService;
import com.matsinger.barofishserver.data.topbar.TopBar;
import com.matsinger.barofishserver.data.topbar.TopBarService;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.product.object.Product;
import com.matsinger.barofishserver.product.ProductService;
import com.matsinger.barofishserver.siteInfo.SiteInfoService;
import com.matsinger.barofishserver.siteInfo.SiteInformation;
import com.matsinger.barofishserver.store.StoreRepository;
import com.matsinger.barofishserver.store.StoreService;
import com.matsinger.barofishserver.store.object.SimpleStore;
import com.matsinger.barofishserver.store.object.StoreInfo;
import com.matsinger.barofishserver.store.StoreInfoRepository;
import com.matsinger.barofishserver.utils.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/main")
public class MainController {

    private final TopBarService topBarService;
    private final BannerService bannerService;
    private final CurationService curationService;
    private final TipService tipService;

    private final SiteInfoService siteInfoService;

    private final ProductService productService;

    private final StoreInfoRepository storeInfoRepository;
    private final StoreService storeService;
    private final JwtService jwt;

    @GetMapping("")
    public ResponseEntity<CustomResponse<Main>> selectMainItems(@RequestHeader(value = "Authorization") Optional<String> auth) {
        CustomResponse<Main> res = new CustomResponse();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ALLOW), auth);
        try {
            Main data = new Main();
            List<TopBar> topBars = topBarService.selectTopBarList();
            List<Banner> banners = bannerService.selectBannerList();
            List<Curation> curations = curationService.selectCurations();
            List<CurationDto> curationDtos = new ArrayList<>();
            for (Curation curation : curations) {
                List<Product> products = curationService.selectCurationProducts(curation.getId());
                CurationDto curationDto = curation.convert2Dto();
                curationDto.setProducts(products.stream().map(productService::convert2ListDto).toList());
                curationDtos.add(curationDto);

            }
            SiteInformation siteInfo = siteInfoService.selectSiteInfo("INTERNAL_MAIN_STORE");
            Integer storeId = Integer.valueOf(siteInfo.getContent());
            StoreInfo store = storeInfoRepository.findById(storeId).orElse(null);

            SimpleStore simpleStore = store != null ? store.convert2Dto() : null;
            if (simpleStore != null) simpleStore.setIsLike(tokenInfo != null &&
                    tokenInfo.isPresent() &&
                    tokenInfo.get().getType().equals(TokenAuthType.USER) ? storeService.checkLikeStore(storeId,
                    tokenInfo.get().getId()) : false);
            List<Banner> subBanners = bannerService.selectMainBanner();
            data.setSubBanner(subBanners);
            data.setCurations(curationDtos);
            data.setBanners(banners);
            data.setTopBars(topBars);
            data.setStore(simpleStore);
            res.setData(Optional.of(data));

            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }
}
