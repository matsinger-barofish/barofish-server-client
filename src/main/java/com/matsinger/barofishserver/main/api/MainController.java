package com.matsinger.barofishserver.main.api;

import com.matsinger.barofishserver.banner.application.BannerQueryService;
import com.matsinger.barofishserver.banner.domain.Banner;
import com.matsinger.barofishserver.data.curation.application.CurationQueryService;
import com.matsinger.barofishserver.data.curation.domain.Curation;
import com.matsinger.barofishserver.data.curation.dto.CurationDto;
import com.matsinger.barofishserver.data.topbar.application.TopBarQueryService;
import com.matsinger.barofishserver.data.topbar.domain.TopBar;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.main.dto.Main;
import com.matsinger.barofishserver.product.domain.Product;
import com.matsinger.barofishserver.product.application.ProductService;
import com.matsinger.barofishserver.store.application.StoreService;
import com.matsinger.barofishserver.store.dto.SimpleStore;
import com.matsinger.barofishserver.store.repository.StoreInfoRepository;
import com.matsinger.barofishserver.utils.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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

    private final TopBarQueryService topBarQueryService;
    private final BannerQueryService bannerService;
    private final CurationQueryService curationQueryService;
    private final ProductService productService;

    private final StoreInfoRepository storeInfoRepository;
    private final StoreService storeService;
    private final JwtService jwt;

    @GetMapping("")
    public ResponseEntity<CustomResponse<Main>> selectMainItems(@RequestHeader(value = "Authorization") Optional<String> auth) {
        CustomResponse<Main> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ALLOW), auth);
        try {
            Main data = new Main();
            List<TopBar> topBars = topBarQueryService.selectTopBarList();
            List<Banner> banners = bannerService.selectBannerList();
            List<Curation> curations = curationQueryService.selectCurations();
            List<Banner> subBanners = bannerService.selectMainBanner();
            data.setSubBanner(subBanners);
            data.setBanners(banners);
            data.setTopBars(topBars);
            res.setData(Optional.of(data));

            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/curation")
    public ResponseEntity<CustomResponse<List<CurationDto>>> selectMainCurationList() {
        CustomResponse<List<CurationDto>> res = new CustomResponse<>();
        try {
            List<Curation> curations = curationQueryService.selectCurations();
            List<CurationDto> curationDtos = new ArrayList<>();
            for (Curation curation : curations) {
                List<Product>
                        products =
                        curationQueryService.selectCurationProducts(curation.getId(), PageRequest.of(0, 10));
                CurationDto curationDto = curation.convert2Dto();
                curationDto.setProducts(products.stream().map(productService::convert2ListDto).toList());
                curationDtos.add(curationDto);
            }
            res.setData(Optional.of(curationDtos));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/store")
    public ResponseEntity<CustomResponse<List<SimpleStore>>> selectMainStoreList(@RequestHeader(value = "Authorization") Optional<String> auth) {
        CustomResponse<List<SimpleStore>> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ALLOW), auth);
        try {
            Integer userId = null;
            if (tokenInfo != null && tokenInfo.isPresent() && tokenInfo.get().getType().equals(TokenAuthType.USER))
                userId = tokenInfo.get().getId();
            Integer finalUserId = userId;
            List<SimpleStore> storeDtos = storeService.selectReliableStoreRandomOrder().stream().map(v -> {
                SimpleStore store = storeService.convert2SimpleDto(v, finalUserId);
                return store;
            }).toList();
            res.setData(Optional.of(storeDtos));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }
}
