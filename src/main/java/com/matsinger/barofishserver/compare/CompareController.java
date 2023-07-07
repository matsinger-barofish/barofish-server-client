package com.matsinger.barofishserver.compare;

import com.matsinger.barofishserver.category.Category;
import com.matsinger.barofishserver.category.CategoryService;
import com.matsinger.barofishserver.compare.obejct.*;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.product.ProductService;
import com.matsinger.barofishserver.product.object.Product;
import com.matsinger.barofishserver.product.object.ProductListDto;
import com.matsinger.barofishserver.utils.CustomResponse;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/compare")
public class CompareController {

    private final JwtService jwt;

    private final CompareItemService compareService;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final RecommendCompareSetService recommendCompareSetService;


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
            List<Product> products = productService.selectNewerProductList(page, take, null, null).getContent();
            for (Product p : products) {
                ProductListDto pl = productService.convert2ListDto(p);
                pl.setIsLike(compareService.checkSaveProduct(userId, p.getId()));
                productListDtos.add(pl);
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
        CustomResponse<List<CompareObject.CompareSetDto>> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return ResponseEntity.ok(res);
        try {
            List<CompareSet> compareSets = compareService.selectCompareSetList(tokenInfo.get().getId());
            List<CompareObject.CompareSetDto> compareSetDtos = compareSets.stream().map(compareSet -> {
                List<ProductListDto>
                        products =
                        compareService.selectCompareItems(compareSet.getId()).stream().map(productService::convert2ListDto).toList();
                CompareObject.CompareSetDto compareSetDto = new CompareObject.CompareSetDto();
                compareSetDto.setCompareSetId(compareSet.getId());
                compareSetDto.setProducts(products);
                return compareSetDto;
            }).toList();
            res.setData(Optional.of(compareSetDtos));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/set/{id}")
    public ResponseEntity<CustomResponse<List<CompareProductDto>>> selectCompareSet(@RequestHeader("Authorization") Optional<String> auth,
                                                                                    @PathVariable("id") Integer id) {
        CustomResponse<List<CompareProductDto>> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            CompareSet compareSet = compareService.selectCompareSet(id);
            List<CompareProductDto>
                    products =
                    compareService.selectCompareItems(compareSet.getId()).stream().map(compareService::convertProduct2Dto).toList();
            res.setData(Optional.of(products));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/save")
    public ResponseEntity<CustomResponse<List<ProductListDto>>> selectSaveProductList(@RequestHeader("Authorization") Optional<String> auth) {
        CustomResponse<List<ProductListDto>> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            List<ProductListDto>
                    products =
                    compareService.selectSaveProducts(tokenInfo.get().getId()).stream().map(productService::convert2ListDto).toList();
            res.setData(Optional.of(products));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @PostMapping("/add-set")
    public ResponseEntity<CustomResponse<CompareSet>> addCompareSet(@RequestHeader("Authorization") Optional<String> auth,
                                                                    @RequestBody List<Integer> productIds) {
        CustomResponse<CompareSet> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            if (productIds.size() != 3) return res.throwError("비교하기는 3개의 상품만 가능합니다.", "INPUT_CHECK_REQUIRED");
            List<Product> products = productService.selectProductListWithIds(productIds);
            if (products.stream().map(Product::getCategoryId).map(v -> {
                Category category = categoryService.findById(v);
                return category.getCategoryId();
            }).collect(Collectors.toSet()).size() != 1)
                return res.throwError("같은 카테고리의 상품끼리 비교 가능합니다.", "INPUT_CHECK_REQUIRED");
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
        CustomResponse<Boolean> res = new CustomResponse<>();
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
        CustomResponse<Boolean> res = new CustomResponse<>();
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
        CustomResponse<Boolean> res = new CustomResponse<>();
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

    //------추천 비교하기 세트-------------------------------
    @GetMapping("/recommend/list")
    public ResponseEntity<CustomResponse<List<RecommendCompareSetDto>>> selectRecommendCompareSetListByAdmin(@RequestHeader("Authorization") Optional<String> auth,
                                                                                                             @RequestParam(value = "type") RecommendCompareSetType type) {
        CustomResponse<List<RecommendCompareSetDto>> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            List<RecommendCompareSetDto>
                    recommendCompareSetDtos =
                    recommendCompareSetService.selectRecommendCompareSetList(type).stream().map(v -> recommendCompareSetService.convert2Dto(
                            v,
                            null)).toList();
            res.setData(Optional.of(recommendCompareSetDtos));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/recommend/{id}")
    public ResponseEntity<CustomResponse<RecommendCompareSetDto>> selectRecommendCompareSet(@PathVariable("id") Integer id) {
        CustomResponse<RecommendCompareSetDto> res = new CustomResponse<>();
        try {
            RecommendCompareSetDto
                    recommendCompareSetDto =
                    recommendCompareSetService.convert2Dto(recommendCompareSetService.selectRecommendCompareSet(id),
                            null);
            res.setData(Optional.ofNullable(recommendCompareSetDto));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @Getter
    @NoArgsConstructor
    private static class AddRecommendCompareSet {
        RecommendCompareSetType type;
        List<Integer> productIds;
    }

    @PostMapping("/recommend/add")
    public ResponseEntity<CustomResponse<RecommendCompareSetDto>> addRecommendCompareSet(@RequestHeader("Authorization") Optional<String> auth,
                                                                                         @RequestPart(value = "data") AddRecommendCompareSet data) {
        CustomResponse<RecommendCompareSetDto> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            if (data.type == null) return res.throwError("타입을 입력해주세요.", "INPUT_CHECK_REQUIRED");
            if (data.productIds == null) return res.throwError("상품 아이디를 입력해주세요.", "INPUT_CHECK_REQUIRED");
            if (new HashSet<>(data.productIds).size() != 3)
                return res.throwError("3개의 상품을 입력해주세요.", "INPUT_CHECK_REQUIRED");
            List<Product> products = data.productIds.stream().map(productService::selectProduct).toList();
            if (products.stream().map(v -> v.getCategory().getCategoryId()).collect(Collectors.toSet()).size() != 1)
                return res.throwError("같은 카테고리 내에서 선정 가능합니다.", "INPUT_CHECK_REQUIRED");

            RecommendCompareSet
                    recommendCompareSet =
                    recommendCompareSetService.addRecommendCompareSet(RecommendCompareSet.builder().type(data.type).product1Id(
                            data.productIds.get(0)).product2Id(data.productIds.get(1)).product3Id(data.productIds.get(2)).build());
            res.setData(Optional.ofNullable(recommendCompareSetService.convert2Dto(recommendCompareSet, null)));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @PostMapping("/recommend/update/{id}")
    public ResponseEntity<CustomResponse<RecommendCompareSetDto>> updateRecommendCompareSet(@RequestHeader("Authorization") Optional<String> auth,
                                                                                            @RequestPart(value = "data") AddRecommendCompareSet data,
                                                                                            @PathVariable("id") Integer id) {
        CustomResponse<RecommendCompareSetDto> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            RecommendCompareSet set = recommendCompareSetService.selectRecommendCompareSet(id);
            if (data.productIds == null) return res.throwError("상품 아이디를 입력해주세요.", "INPUT_CHECK_REQUIRED");
            if (data.type != null) {
                set.setType(data.type);
            }
            if (new HashSet<>(data.productIds).size() != 3)
                return res.throwError("3개의 상품을 입력해주세요.", "INPUT_CHECK_REQUIRED");
            List<Product> products = data.productIds.stream().map(productService::selectProduct).toList();
            if (products.stream().map(v -> v.getCategory().getCategoryId()).collect(Collectors.toSet()).size() != 1)
                return res.throwError("같은 카테고리 내에서 선정 가능합니다.", "INPUT_CHECK_REQUIRED");
            set.setProduct1Id(data.productIds.get(0));
            set.setProduct2Id(data.productIds.get(1));
            set.setProduct3Id(data.productIds.get(2));
            set = recommendCompareSetService.updateRecommendCompareSet(set);
            RecommendCompareSet recommendCompareSet = recommendCompareSetService.addRecommendCompareSet(set);
            res.setData(Optional.ofNullable(recommendCompareSetService.convert2Dto(recommendCompareSet, null)));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @DeleteMapping("/recommend/{id}")
    public ResponseEntity<CustomResponse<Boolean>> deleteRecommendCompareSet(@RequestHeader("Authorization") Optional<String> auth,
                                                                             @PathVariable("id") Integer id) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            RecommendCompareSet set = recommendCompareSetService.selectRecommendCompareSet(id);
            recommendCompareSetService.deleteRecommendCompareSet(id);
            res.setData(Optional.of(true));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }
}
