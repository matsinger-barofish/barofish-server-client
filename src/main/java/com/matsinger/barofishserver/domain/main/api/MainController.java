package com.matsinger.barofishserver.domain.main.api;

import com.matsinger.barofishserver.domain.banner.application.BannerQueryService;
import com.matsinger.barofishserver.domain.banner.domain.Banner;
import com.matsinger.barofishserver.domain.data.curation.application.CurationQueryService;
import com.matsinger.barofishserver.domain.data.curation.domain.Curation;
import com.matsinger.barofishserver.domain.data.curation.domain.CurationState;
import com.matsinger.barofishserver.domain.data.curation.dto.CurationDto;
import com.matsinger.barofishserver.domain.data.topbar.application.TopBarQueryService;
import com.matsinger.barofishserver.domain.data.topbar.domain.TopBar;
import com.matsinger.barofishserver.domain.product.application.ProductService;
import com.matsinger.barofishserver.domain.product.domain.Product;
import com.matsinger.barofishserver.domain.store.application.StoreService;
import com.matsinger.barofishserver.domain.store.dto.SimpleStore;
import com.matsinger.barofishserver.domain.store.repository.StoreInfoRepository;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.domain.main.dto.Main;
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
    }

    @GetMapping("/curation")
    public ResponseEntity<CustomResponse<List<CurationDto>>> selectMainCurationList(@RequestHeader(value = "Authorization", required = false) Optional<String> auth) {
        CustomResponse<List<CurationDto>> res = new CustomResponse<>();

        TokenInfo tokenInfo = null;
        Integer userId = null;

        // 토큰이 없으면 비회원으로 간주
        if (auth.isEmpty()) {
            userId = null;
        }

        if (auth.isPresent()) {
            tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER, TokenAuthType.ALLOW), auth.get());
            userId = tokenInfo.getId();
        }

        List<Curation> curations = curationQueryService.selectCurationState(CurationState.ACTIVE);
        List<CurationDto> curationDtos = new ArrayList<>();
        for (Curation curation : curations) {
            List<Product>
                    products =
                    curationQueryService.selectCurationProducts(curation.getId(), PageRequest.of(0, 10));
            CurationDto curationDto = curation.convert2Dto();

            Integer finalUserId = userId;
            curationDto.setProducts(products.stream().map(v -> productService.convert2ListDto(v, finalUserId)).toList());
            curationDtos.add(curationDto);
        }
        res.setData(Optional.of(curationDtos));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/store")
    public ResponseEntity<CustomResponse<List<SimpleStore>>> selectMainStoreList(@RequestHeader(value = "Authorization") Optional<String> auth) {
        CustomResponse<List<SimpleStore>> res = new CustomResponse<>();

        TokenInfo tokenInfo = null;
        Integer userId = null;

        // 토큰이 없으면 비회원으로 간주
        if (auth.isEmpty()) {
            userId = null;
        }
        // 토큰이 있을 때 만료됐으면 비회원으로 간주, 정상적이면 로그인으로 간주
        if (auth.isPresent()) {
            tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER, TokenAuthType.ALLOW), auth.get());
            userId = tokenInfo.getId();
        }

        Integer finalUserId = userId;
        List<SimpleStore> storeDtos = storeService.selectReliableStoreRandomOrder().stream().map(v -> {
            SimpleStore store = storeService.convert2SimpleDto(v, finalUserId);
            return store;
        }).toList();
        res.setData(Optional.of(storeDtos));
        return ResponseEntity.ok(res);
    }
}
