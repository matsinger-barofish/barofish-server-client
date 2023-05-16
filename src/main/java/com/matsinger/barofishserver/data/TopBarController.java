package com.matsinger.barofishserver.data;

import com.matsinger.barofishserver.product.Product;
import com.matsinger.barofishserver.product.ProductService;
import com.matsinger.barofishserver.utils.Common;
import com.matsinger.barofishserver.utils.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/topbar")
public class TopBarController {

    private final TopBarService topBarService;
    private final ProductService productService;
    private final Common utils;

    @GetMapping("")
    public ResponseEntity<CustomResponse> selectTopBarList() {
        CustomResponse res = new CustomResponse();
        try {
            List<TopBar> topBarList = topBarService.selectTopBarList();
            res.setData(Optional.ofNullable(topBarList));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            res.setIsSuccess(false);
            res.setErrorMsg(e.getMessage());
            return ResponseEntity.ok(res);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomResponse> selectTopBar(@PathVariable("id") Integer id) {
        CustomResponse res = new CustomResponse();
        try {
            TopBar topBar = topBarService.selectTopBar(id);
            res.setData(Optional.ofNullable(topBar));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            res.setIsSuccess(false);
            res.setErrorMsg(e.getMessage());
            return ResponseEntity.ok(res);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<CustomResponse> addTopBar(@RequestPart(value = "name") String name) {
        CustomResponse res = new CustomResponse();
        try {
            name = utils.validateString(name, 20L, "이름");
            TopBar topBar = new TopBar();
            topBar.setName(name);
            TopBar result = topBarService.add(topBar);
            res.setData(Optional.ofNullable(result));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            res.setIsSuccess(false);
            res.setErrorMsg(e.getMessage());
            return ResponseEntity.ok(res);
        }
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<CustomResponse> updateTopBar(@PathVariable("id") Integer id,
                                                       @RequestPart(value = "name") String name) {
        CustomResponse res = new CustomResponse();
        try {
            TopBar topbar = topBarService.selectTopBar(id);
            name = utils.validateString(name, 20L, "이름");
            topbar.setName(name);
            TopBar result = topBarService.update(id, topbar);
            res.setData(Optional.ofNullable(result));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            res.setIsSuccess(false);
            res.setErrorMsg(e.getMessage());
            return ResponseEntity.ok(res);
        }
    }

    @PostMapping("/add-product")
    public ResponseEntity<CustomResponse> addProductToTopBar(@RequestPart(value = "topBarId") Integer topBarId,
                                                             @RequestPart(value = "productId") Integer productId) {
        CustomResponse res = new CustomResponse();
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
            res.setIsSuccess(false);
            res.setErrorMsg(e.getMessage());
            return ResponseEntity.ok(res);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse> deleteTopBar(@PathVariable("id") Integer id) {
        CustomResponse res = new CustomResponse();
        try {
            TopBar topBar = topBarService.selectTopBar(id);
            topBarService.delete(id);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            res.setIsSuccess(false);
            res.setErrorMsg(e.getMessage());
            return ResponseEntity.ok(res);
        }
    }
}
