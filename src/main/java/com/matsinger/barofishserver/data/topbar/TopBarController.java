package com.matsinger.barofishserver.data.topbar;

import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.product.object.Product;
import com.matsinger.barofishserver.product.ProductService;
import com.matsinger.barofishserver.product.object.ProductListDto;
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

    private final TopBarService topBarService;
    private final ProductService productService;

    private final Common utils;

    private final JwtService jwt;

    @GetMapping("")
    public ResponseEntity<CustomResponse<List<TopBar>>> selectTopBarList() {
        CustomResponse<List<TopBar>> res = new CustomResponse<>();
        try {
            List<TopBar> topBarList = topBarService.selectTopBarList();
            res.setData(Optional.ofNullable(topBarList));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
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
        try {
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
        } catch (Exception e) {
            return res.defaultError(e);
        }
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
        try {
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
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<CustomResponse<TopBar>> addTopBar(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                            @RequestPart(value = "name") String name) {
        CustomResponse<TopBar> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            name = utils.validateString(name, 20L, "이름");
            TopBar topBar = new TopBar();
            topBar.setName(name);
            TopBar result = topBarService.add(topBar);
            res.setData(Optional.ofNullable(result));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<CustomResponse<TopBar>> updateTopBar(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                               @PathVariable("id") Integer id,
                                                               @RequestPart(value = "name") String name) {
        CustomResponse<TopBar> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            TopBar topbar = topBarService.selectTopBar(id);
            name = utils.validateString(name, 20L, "이름");
            topbar.setName(name);
            TopBar result = topBarService.update(id, topbar);
            res.setData(Optional.ofNullable(result));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @PostMapping("/add-product")
    public ResponseEntity<CustomResponse<TopBarProductMap>> addProductToTopBar(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                               @RequestPart(value = "topBarId") Integer topBarId,
                                                                               @RequestPart(value = "productId") Integer productId) {
        CustomResponse<TopBarProductMap> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            TopBar topBar = topBarService.selectTopBar(topBarId);
            Product product = productService.selectProduct(productId);
            TopBarProductMap data = new TopBarProductMap();
            data.setTopBar(topBar);
            data.setProduct(product);
            TopBarProductMap result = topBarService.addProduct(data);
            res.setData(Optional.ofNullable(result));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse<Boolean>> deleteTopBar(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                @PathVariable("id") Integer id) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            TopBar topBar = topBarService.selectTopBar(id);
            Boolean result = topBarService.delete(id);
            res.setData(Optional.ofNullable(result));
            res.setIsSuccess(result);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }
}
