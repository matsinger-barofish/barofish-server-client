package com.matsinger.barofishserver.product.productinfo;

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
@RequestMapping("/api/v1/product-info")
public class ProductInfoController {

    private final ProductInfoService productInfoService;

    @GetMapping("/type")
    public ResponseEntity<CustomResponse<List<ProductType>>> selectTypeList() {
        CustomResponse<List<ProductType>> res = new CustomResponse<>();
        try {
            List<ProductType> data = productInfoService.selectProductTypeList();
            res.setData(Optional.ofNullable(data));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/location")
    public ResponseEntity<CustomResponse<List<ProductLocation>>> selectLocationList() {
        CustomResponse<List<ProductLocation>> res = new CustomResponse<>();
        try {
            List<ProductLocation> data = productInfoService.selectProductLocationList();
            res.setData(Optional.ofNullable(data));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/process")
    public ResponseEntity<CustomResponse<List<ProductProcess>>> selectProcessList() {
        CustomResponse<List<ProductProcess>> res = new CustomResponse<>();
        try {
            List<ProductProcess> data = productInfoService.selectProductProcessList();
            res.setData(Optional.ofNullable(data));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/usage")
    public ResponseEntity<CustomResponse<List<ProductUsage>>> selectUsageList() {
        CustomResponse<List<ProductUsage>> res = new CustomResponse<>();
        try {
            List<ProductUsage> data = productInfoService.selectProductUsageList();
            res.setData(Optional.ofNullable(data));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/storage")
    public ResponseEntity<CustomResponse<List<ProductStorage>>> selectStorageList() {
        CustomResponse<List<ProductStorage>> res = new CustomResponse<>();
        try {
            List<ProductStorage> data = productInfoService.selectProductStorageList();
            res.setData(Optional.ofNullable(data));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }
}
