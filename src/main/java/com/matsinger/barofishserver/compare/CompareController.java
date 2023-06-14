package com.matsinger.barofishserver.compare;

import com.matsinger.barofishserver.compare.obejct.CompareMain;
import com.matsinger.barofishserver.compare.obejct.CompareObject;
import com.matsinger.barofishserver.compare.obejct.CompareSet;
import com.matsinger.barofishserver.compare.obejct.SaveProduct;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.product.ProductService;
import com.matsinger.barofishserver.product.object.Product;
import com.matsinger.barofishserver.product.object.ProductListDto;
import com.matsinger.barofishserver.utils.CustomResponse;
import lombok.*;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/compare")
public class CompareController {

    private final JwtService jwt;

    private final CompareItemService compareService;
    private final ProductService productService;


    @GetMapping("/test")
    public ResponseEntity<CustomResponse<List<CompareObject.CompareSetDto>>> test() {
        CustomResponse<List<CompareObject.CompareSetDto>> res = new CustomResponse<>();
        try {
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/main")
    public ResponseEntity<CustomResponse<CompareMain>> selectMain(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                  @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                                                  @RequestParam(value = "take", required = false, defaultValue = "10") Integer take) {
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        CustomResponse<CompareMain> res = new CustomResponse<>();
        try {
            Integer
                    userId =
                    tokenInfo != null &&
                            tokenInfo.isPresent() &&
                            tokenInfo.get().getType().equals(TokenAuthType.USER) ? tokenInfo.get().getId() : null;
            CompareMain mainData = new CompareMain();
            mainData.setRecommendCompareProducts(compareService.selectRecommendCompareSetList(userId));
            mainData.setPopularCompareSets(compareService.selectPopularCompareSetList(userId));
            List<ProductListDto> productListDtos = new ArrayList<>();
            List<Product>
                    products =
                    productService.selectNewerProductList(page, take, null, null, null, null, null, null);
            for (Product p : products) {
                productListDtos.add(p.convert2ListDto());
            }
            mainData.setNewCompareProduct(new CompareObject.NewCompareProduct(productListDtos));
            res.setData(Optional.of(mainData));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/set/list")
    public ResponseEntity<CustomResponse<List<CompareObject.CompareSetDto>>> selectCompareSetList(@RequestHeader("Authorization") Optional<String> auth) {
        CustomResponse<List<CompareObject.CompareSetDto>> res = new CustomResponse();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return ResponseEntity.ok(res);
        try {
            List<CompareSet> compareSets = compareService.selectCompareSetList(tokenInfo.get().getId());
            List<CompareObject.CompareSetDto> compareSetDtos = compareSets.stream().map(compareSet -> {
                List<ProductListDto>
                        products =
                        compareService.selectCompareItems(compareSet.getId()).stream().map(product -> {
                            return product.convert2ListDto();
                        }).toList();
                CompareObject.CompareSetDto compareSetDto = new CompareObject.CompareSetDto();
                compareSetDto.setCompareSetId(compareSet.getId());
                compareSetDto.setProducts(products);
                return compareSetDto;
            }).toList();
            res.setData(Optional.ofNullable(compareSetDtos));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/set/{id}")
    public ResponseEntity<CustomResponse<CompareObject.CompareSetDto>> selectCompareSet(@RequestHeader("Authorization") Optional<String> auth,
                                                                                        @PathVariable("id") Integer id) {
        CustomResponse<CompareObject.CompareSetDto> res = new CustomResponse();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            CompareSet compareSet = compareService.selectCompareSet(id);
            List<ProductListDto>
                    products =
                    compareService.selectCompareItems(compareSet.getId()).stream().map(product -> {
                        return product.convert2ListDto();
                    }).toList();

            CompareObject.CompareSetDto compareSetDto = new CompareObject.CompareSetDto();
            compareSetDto.setCompareSetId(compareSet.getId());
            compareSetDto.setProducts(products);
            res.setData(Optional.ofNullable(compareSetDto));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/save")
    public ResponseEntity<CustomResponse<List<ProductListDto>>> selectSaveProductList(@RequestHeader("Authorization") Optional<String> auth) {
        CustomResponse<List<ProductListDto>> res = new CustomResponse();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            List<ProductListDto> products =
                    compareService.selectSaveProducts(tokenInfo.get().getId()).stream().map(product -> {
                        return product.convert2ListDto();
                    }).toList();
            res.setData(Optional.ofNullable(products));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @PostMapping("/add-set")
    public ResponseEntity<CustomResponse<CompareSet>> addCompareSet(@RequestHeader("Authorization") Optional<String> auth,
                                                                    @RequestBody List<Integer> productIds) {
        CustomResponse<CompareSet> res = new CustomResponse();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            if (productIds.size() != 3) return res.throwError("비교하기는 3개의 상품만 가능합니다.", "INPUT_CHECK_REQUIRED");
            CompareSet compareSet = compareService.addCompareSet(tokenInfo.get().getId(), productIds);
            res.setData(Optional.ofNullable(compareSet));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    static class SaveProductReq {
        private Integer productId;
    }

    @PostMapping("/save-product")
    public ResponseEntity<CustomResponse<Boolean>> saveProduct(@RequestHeader("Authorization") Optional<String> auth,
                                                               @RequestPart(value = "data") SaveProductReq data) {
        CustomResponse<Boolean> res = new CustomResponse();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Boolean result = compareService.addSaveProduct(tokenInfo.get().getId(), data.getProductId());
            res.setData(Optional.ofNullable(result));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @Getter
    @NoArgsConstructor
    static class DeleteCompareSetReq {
        private List<Integer> compareSetIds;
    }

    @DeleteMapping("/set")
    public ResponseEntity<CustomResponse<Boolean>> deleteCompareSet(@RequestHeader("Authorization") Optional<String> auth,
                                                                    @RequestPart(value = "data") DeleteCompareSetReq data) {
        CustomResponse<Boolean> res = new CustomResponse();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            for (Integer setId : data.getCompareSetIds()) {
                CompareSet compareSet = compareService.selectCompareSet(setId);
                if (tokenInfo.get().getId() != compareSet.getUserId())
                    return res.throwError("삭제 권한이 없습니다.", "NOT_ALLOWED");
            }
            for (Integer setId : data.getCompareSetIds()) {
                compareService.deleteCompareSet(setId);
            }
            res.setData(Optional.of(true));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @Getter
    @NoArgsConstructor
    static class DeleteSaveProductReq {
        private List<Integer> productIds;
    }

    @DeleteMapping("/save")
    public ResponseEntity<CustomResponse<Boolean>> deleteSaveProducts(@RequestHeader("Authorization") Optional<String> auth,
                                                                      @RequestPart(value = "data") DeleteSaveProductReq data) {
        CustomResponse<Boolean> res = new CustomResponse();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            List<SaveProduct> saveProducts = new ArrayList<>();
            for (Integer productId : data.getProductIds()) {
                saveProducts.add(compareService.selectSaveProduct(tokenInfo.get().getId(), productId));
            }
            compareService.deleteSaveProduct(saveProducts);
            res.setData(Optional.of(true));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }
}
