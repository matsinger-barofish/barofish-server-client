package com.matsinger.barofishserver.product.productinfo;

import com.matsinger.barofishserver.data.curation.object.CurationDto;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.utils.Common;
import com.matsinger.barofishserver.utils.CustomResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product-info")
public class ProductInfoController {

    private final ProductInfoService productInfoService;
    private final JwtService jwt;
    private final Common utils;

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

    @Getter
    @NoArgsConstructor
    private static class AddProductInfoReq {
        String field;
    }

    @Getter
    @NoArgsConstructor
    private static class DeleteProductInfoReq {
        List<Integer> ids;
    }

    @PostMapping("/type/add")
    public ResponseEntity<CustomResponse<ProductType>> addProductType(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                      @RequestPart(value = "data") AddProductInfoReq data) {
        CustomResponse<ProductType> res = new CustomResponse();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            String name = utils.validateString(data.field, 20L, "필드명");
            ProductType productType = ProductType.builder().field(name).build();
            productInfoService.addProductType(productType);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @PostMapping("/type/update/{id}")
    public ResponseEntity<CustomResponse<ProductType>> updateProductType(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                         @PathVariable("id") Integer id,
                                                                         @RequestPart(value = "data") AddProductInfoReq data) {
        CustomResponse<ProductType> res = new CustomResponse();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            ProductType productType = productInfoService.selectProductType(id);
            if (data.field != null) {
                String field = utils.validateString(data.field, 20L, "이름");
                productType.setField(field);
            }
            productType = productInfoService.updateProductType(productType);
            res.setData(Optional.ofNullable(productType));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @DeleteMapping("/type/delete")
    public ResponseEntity<CustomResponse<Boolean>> deleteProductTypes(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                      @RequestPart(value = "data") DeleteProductInfoReq data) {
        CustomResponse<Boolean> res = new CustomResponse();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            if (data.ids == null) return res.throwError("아이디를 입릭해주세요.", "INPUT_CHECK_REQUIRED");
            productInfoService.deleteProductType(data.ids);
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

    @PostMapping("/location/add")
    public ResponseEntity<CustomResponse<ProductLocation>> addProductLocation(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                              @RequestPart(value = "data") AddProductInfoReq data) {
        CustomResponse<ProductLocation> res = new CustomResponse();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            String name = utils.validateString(data.field, 20L, "필드명");
            ProductLocation productLocation = ProductLocation.builder().field(name).build();
            productInfoService.addProductLocation(productLocation);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @PostMapping("/location/update/{id}")
    public ResponseEntity<CustomResponse<ProductLocation>> updateProductLocation(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                                 @PathVariable("id") Integer id,
                                                                                 @RequestPart(value = "data") AddProductInfoReq data) {
        CustomResponse<ProductLocation> res = new CustomResponse();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            ProductLocation productLocation = productInfoService.selectProductLocation(id);
            if (data.field != null) {
                String field = utils.validateString(data.field, 20L, "이름");
                productLocation.setField(field);
            }
            productLocation = productInfoService.updateProductLocation(productLocation);
            res.setData(Optional.ofNullable(productLocation));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @DeleteMapping("/location/delete")
    public ResponseEntity<CustomResponse<Boolean>> deleteProductLocations(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                          @RequestPart(value = "data") DeleteProductInfoReq data) {
        CustomResponse<Boolean> res = new CustomResponse();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            if (data.ids == null) return res.throwError("아이디를 입릭해주세요.", "INPUT_CHECK_REQUIRED");
            productInfoService.deleteProductLocation(data.ids);
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

    @PostMapping("/process/add")
    public ResponseEntity<CustomResponse<ProductProcess>> addProductProcess(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                            @RequestPart(value = "data") AddProductInfoReq data) {
        CustomResponse<ProductProcess> res = new CustomResponse();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            String name = utils.validateString(data.field, 20L, "필드명");
            ProductProcess productProcess = ProductProcess.builder().field(name).build();
            productInfoService.addProductProcess(productProcess);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @PostMapping("/process/update/{id}")
    public ResponseEntity<CustomResponse<ProductProcess>> updateProductProcess(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                               @PathVariable("id") Integer id,
                                                                               @RequestPart(value = "data") AddProductInfoReq data) {
        CustomResponse<ProductProcess> res = new CustomResponse();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            ProductProcess productProcess = productInfoService.selectProductProcess(id);
            if (data.field != null) {
                String field = utils.validateString(data.field, 20L, "이름");
                productProcess.setField(field);
            }
            productProcess = productInfoService.updateProductProcess(productProcess);
            res.setData(Optional.ofNullable(productProcess));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @DeleteMapping("/process/delete")
    public ResponseEntity<CustomResponse<Boolean>> deleteProductProcesss(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                         @RequestPart(value = "data") DeleteProductInfoReq data) {
        CustomResponse<Boolean> res = new CustomResponse();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            if (data.ids == null) return res.throwError("아이디를 입릭해주세요.", "INPUT_CHECK_REQUIRED");
            productInfoService.deleteProductProcess(data.ids);
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

    @PostMapping("/usage/add")
    public ResponseEntity<CustomResponse<ProductUsage>> addProductUsage(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                        @RequestPart(value = "data") AddProductInfoReq data) {
        CustomResponse<ProductUsage> res = new CustomResponse();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            String name = utils.validateString(data.field, 20L, "필드명");
            ProductUsage productUsage = ProductUsage.builder().field(name).build();
            productInfoService.addProductUsage(productUsage);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @PostMapping("/usage/update/{id}")
    public ResponseEntity<CustomResponse<ProductUsage>> updateProductUsage(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                           @PathVariable("id") Integer id,
                                                                           @RequestPart(value = "data") AddProductInfoReq data) {
        CustomResponse<ProductUsage> res = new CustomResponse();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            ProductUsage productUsage = productInfoService.selectProductUsage(id);
            if (data.field != null) {
                String field = utils.validateString(data.field, 20L, "이름");
                productUsage.setField(field);
            }
            productUsage = productInfoService.updateProductUsage(productUsage);
            res.setData(Optional.ofNullable(productUsage));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @DeleteMapping("/usage/delete")
    public ResponseEntity<CustomResponse<Boolean>> deleteProductUsages(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                       @RequestPart(value = "data") DeleteProductInfoReq data) {
        CustomResponse<Boolean> res = new CustomResponse();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            if (data.ids == null) return res.throwError("아이디를 입릭해주세요.", "INPUT_CHECK_REQUIRED");
            productInfoService.deleteProductUsage(data.ids);
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

    @PostMapping("/storage/add")
    public ResponseEntity<CustomResponse<ProductStorage>> addProductStorage(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                            @RequestPart(value = "data") AddProductInfoReq data) {
        CustomResponse<ProductStorage> res = new CustomResponse();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            String name = utils.validateString(data.field, 20L, "필드명");
            ProductStorage productStorage = ProductStorage.builder().field(name).build();
            productInfoService.addProductStorage(productStorage);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @PostMapping("/storage/update/{id}")
    public ResponseEntity<CustomResponse<ProductStorage>> updateProductStorage(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                               @PathVariable("id") Integer id,
                                                                               @RequestPart(value = "data") AddProductInfoReq data) {
        CustomResponse<ProductStorage> res = new CustomResponse();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            ProductStorage productStorage = productInfoService.selectProductStorage(id);
            if (data.field != null) {
                String field = utils.validateString(data.field, 20L, "이름");
                productStorage.setField(field);
            }
            productStorage = productInfoService.updateProductStorage(productStorage);
            res.setData(Optional.ofNullable(productStorage));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @DeleteMapping("/storage/delete")
    public ResponseEntity<CustomResponse<Boolean>> deleteProductStorages(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                         @RequestPart(value = "data") DeleteProductInfoReq data) {
        CustomResponse<Boolean> res = new CustomResponse();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            if (data.ids == null) return res.throwError("아이디를 입릭해주세요.", "INPUT_CHECK_REQUIRED");
            productInfoService.deleteProductStorage(data.ids);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }
}
