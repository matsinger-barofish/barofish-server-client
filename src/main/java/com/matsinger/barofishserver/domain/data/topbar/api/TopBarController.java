package com.matsinger.barofishserver.domain.data.topbar.api;

import com.matsinger.barofishserver.domain.data.topbar.application.TopBarCommandService;
import com.matsinger.barofishserver.domain.data.topbar.application.TopBarQueryService;
import com.matsinger.barofishserver.domain.data.topbar.domain.TopBarProductMap;
import com.matsinger.barofishserver.domain.data.topbar.domain.TopBar;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.domain.product.domain.Product;
import com.matsinger.barofishserver.domain.product.application.ProductService;
import com.matsinger.barofishserver.domain.product.dto.ProductListDto;
import com.matsinger.barofishserver.utils.Common;
import com.matsinger.barofishserver.utils.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/topbar")
public class TopBarController {

    private final TopBarQueryService topBarQueryService;
    private final TopBarCommandService topBarCommandService;
    private final ProductService productService;

    private final Common utils;

    private final JwtService jwt;

    @GetMapping("")
    public ResponseEntity<CustomResponse<List<TopBar>>> selectTopBarList() {
        CustomResponse<List<TopBar>> res = new CustomResponse<>();

        List<TopBar> topBarList = topBarQueryService.selectTopBarList();
        res.setData(Optional.ofNullable(topBarList));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/{id}/count")
    public ResponseEntity<CustomResponse<Long>> selectTopBarCount(@PathVariable("id") Integer id,
                                                                  @RequestParam(value = "page", defaultValue = "1", required = false) Integer page,
                                                                  @RequestParam(value = "take", defaultValue = "10", required = false) Integer take,
                                                                  @RequestParam(value = "categoryIds", required = false) String categoryIds,
                                                                  @RequestParam(value = "filterFieldIds", required = false) String filterFieldIds,
                                                                  @RequestParam(value = "typeIds", required = false) String typeIds,
                                                                  @RequestParam(value = "locationIds", required = false) String locationIds,
                                                                  @RequestParam(value = "processIds", required = false) String processIds,
                                                                  @RequestParam(value = "usageIds", required = false) String usageIds,
                                                                  @RequestParam(value = "storageIds", required = false) String storageIds) {
        CustomResponse<Long> res = new CustomResponse<>();

        Page<Product> products = switch (id) {
            case 1 -> productService.selectNewerProductList(page - 1,
                    take,
                    utils.str2IntList(categoryIds),
                    utils.str2IntList(filterFieldIds));
            case 2 -> productService.selectPopularProductList(page - 1,
                    take,
                    utils.str2IntList(categoryIds),
                    utils.str2IntList(filterFieldIds));
            default -> productService.selectDiscountProductList(page - 1,
                    take,
                    utils.str2IntList(categoryIds),
                    utils.str2IntList(filterFieldIds));
        };


        res.setData(Optional.of(products.getTotalElements()));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomResponse<Page<ProductListDto>>> selectTopBar(@PathVariable("id") Integer id,
                                                                             @RequestParam(value = "page", defaultValue = "1", required = false) Integer page,
                                                                             @RequestParam(value = "take", defaultValue = "10", required = false) Integer take,
                                                                             @RequestParam(value = "filterFieldIds", required = false) String filterFieldIds,
                                                                             @RequestParam(value = "categoryIds", required = false) String categoryIds,
                                                                             @RequestParam(value = "typeIds", required = false) String typeIds,
                                                                             @RequestParam(value = "locationIds", required = false) String locationIds,
                                                                             @RequestParam(value = "processIds", required = false) String processIds,
                                                                             @RequestParam(value = "usageIds", required = false) String usageIds,
                                                                             @RequestParam(value = "storageIds", required = false) String storageIds) {
        CustomResponse<Page<ProductListDto>> res = new CustomResponse<>();

        Page<Product> products = switch (id) {
            case 1 -> productService.selectNewerProductList(page - 1,
                    take,
                    utils.str2IntList(categoryIds),
                    utils.str2IntList(filterFieldIds));
            case 2 -> productService.selectPopularProductList(page - 1,
                    take,
                    utils.str2IntList(categoryIds),
                    utils.str2IntList(filterFieldIds));
            default -> productService.selectDiscountProductList(page - 1,
                    take,
                    utils.str2IntList(categoryIds),
                    utils.str2IntList(filterFieldIds));
        };


        res.setData(Optional.of(products.map(productService::convert2ListDto)));
        return ResponseEntity.ok(res);
    }

    @PostMapping("/add")
    public ResponseEntity<CustomResponse<TopBar>> addTopBar(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                            @RequestPart(value = "name") String name) throws Exception {
        CustomResponse<TopBar> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");

        name = utils.validateString(name, 20L, "이름");
        TopBar topBar = new TopBar();
        topBar.setName(name);
        TopBar result = topBarCommandService.add(topBar);
        res.setData(Optional.ofNullable(result));
        return ResponseEntity.ok(res);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<CustomResponse<TopBar>> updateTopBar(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                               @PathVariable("id") Integer id,
                                                               @RequestPart(value = "name") String name) throws Exception {
        CustomResponse<TopBar> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");

        TopBar topbar = topBarQueryService.selectTopBar(id);
        name = utils.validateString(name, 20L, "이름");
        topbar.setName(name);
        TopBar result = topBarCommandService.update(id, topbar);
        res.setData(Optional.ofNullable(result));
        return ResponseEntity.ok(res);
    }

    @PostMapping("/add-product")
    public ResponseEntity<CustomResponse<TopBarProductMap>> addProductToTopBar(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                               @RequestPart(value = "topBarId") Integer topBarId,
                                                                               @RequestPart(value = "productId") Integer productId) {
        CustomResponse<TopBarProductMap> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");

        TopBar topBar = topBarQueryService.selectTopBar(topBarId);
        Product product = productService.findById(productId);
        TopBarProductMap data = new TopBarProductMap();
        data.setTopBar(topBar);
        data.setProduct(product);
        TopBarProductMap result = topBarCommandService.addProduct(data);
        res.setData(Optional.ofNullable(result));
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse<Boolean>> deleteTopBar(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                @PathVariable("id") Integer id) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");

        TopBar topBar = topBarQueryService.selectTopBar(id);
        Boolean result = topBarCommandService.delete(id);
        res.setData(Optional.ofNullable(result));
        res.setIsSuccess(result);
        return ResponseEntity.ok(res);
    }
}
