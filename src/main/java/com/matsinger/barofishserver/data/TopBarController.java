package com.matsinger.barofishserver.data;

import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.product.Product;
import com.matsinger.barofishserver.product.ProductService;
import com.matsinger.barofishserver.utils.Common;
import com.matsinger.barofishserver.utils.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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
        CustomResponse<List<TopBar>> res = new CustomResponse();
        try {
            List<TopBar> topBarList = topBarService.selectTopBarList();
            res.setData(Optional.ofNullable(topBarList));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomResponse<TopBar>> selectTopBar(@PathVariable("id") Integer id) {
        CustomResponse<TopBar> res = new CustomResponse();
        try {
            TopBar topBar = topBarService.selectTopBar(id);
            res.setData(Optional.ofNullable(topBar));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<CustomResponse<TopBar>> addTopBar(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                            @RequestPart(value = "name") String name) {
        CustomResponse<TopBar> res = new CustomResponse();
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
        CustomResponse<TopBar> res = new CustomResponse();
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
        CustomResponse<TopBarProductMap> res = new CustomResponse();
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
        CustomResponse<Boolean> res = new CustomResponse();
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
