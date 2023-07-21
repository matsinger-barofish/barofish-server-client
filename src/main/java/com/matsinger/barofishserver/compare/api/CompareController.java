package com.matsinger.barofishserver.compare.api;

import com.matsinger.barofishserver.category.application.CategoryQueryService;
import com.matsinger.barofishserver.category.domain.Category;
import com.matsinger.barofishserver.compare.application.CompareItemCommandService;
import com.matsinger.barofishserver.compare.application.CompareItemQueryService;
import com.matsinger.barofishserver.compare.domain.CompareSet;
import com.matsinger.barofishserver.compare.domain.SaveProduct;
import com.matsinger.barofishserver.compare.dto.*;
import com.matsinger.barofishserver.compare.recommend.domain.RecommendCompareSet;
import com.matsinger.barofishserver.compare.recommend.dto.RecommendCompareSetDto;
import com.matsinger.barofishserver.compare.recommend.application.RecommendCompareSetService;
import com.matsinger.barofishserver.compare.recommend.domain.RecommendCompareSetType;
import com.matsinger.barofishserver.compare.repository.CompareSetRepository;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.product.application.ProductService;
import com.matsinger.barofishserver.product.domain.Product;
import com.matsinger.barofishserver.product.dto.ProductListDto;
import com.matsinger.barofishserver.utils.Common;
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

    private final CompareItemQueryService compareItemQueryService;
    private final CompareItemCommandService compareItemCommandService;
    private final ProductService productService;
    private final CategoryQueryService categoryQueryService;
    private final RecommendCompareSetService recommendCompareSetService;
    private final Common utils;
    private final CompareSetRepository compareSetRepository;


    @GetMapping("/main")
    public ResponseEntity<CustomResponse<CompareMain>> selectMain(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                  @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                                                  @RequestParam(value = "take", required = false, defaultValue = "10") Integer take) {
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        CustomResponse<CompareMain> res = new CustomResponse<>();
        try {
            Integer userId = null;
            if (tokenInfo != null && tokenInfo.isPresent() && tokenInfo.get().getType().equals(TokenAuthType.USER))
                userId = tokenInfo.get().getId();
            CompareMain mainData = new CompareMain();
            mainData.setRecommendCompareProducts(compareItemQueryService.selectRecommendCompareSetList(userId));
            mainData.setPopularCompareSets(compareItemQueryService.selectPopularCompareSetList(userId));
            List<ProductListDto> productListDtos = new ArrayList<>();
            List<Product> products = productService.selectNewerProductList(page, take, null, null).getContent();
            for (Product p : products) {
                ProductListDto pl = productService.convert2ListDto(p);
                pl.setIsLike(compareItemCommandService.checkSaveProduct(userId, p.getId()));
                productListDtos.add(pl);
            }
            mainData.setNewCompareProduct(new NewCompareProduct(productListDtos));
            res.setData(Optional.of(mainData));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/set/list")
    public ResponseEntity<CustomResponse<List<CompareSetDto>>> selectCompareSetList(@RequestHeader("Authorization") Optional<String> auth) {
        CustomResponse<List<CompareSetDto>> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return ResponseEntity.ok(res);
        try {
            List<CompareSet> compareSets = compareItemQueryService.selectCompareSetList(tokenInfo.get().getId());
            List<CompareSetDto> compareSetDtos = compareSets.stream().map(compareSet -> {
                List<ProductListDto>
                        products =
                        compareItemQueryService.selectCompareItems(compareSet.getId()).stream().map(productService::convert2ListDto).toList();
                CompareSetDto compareSetDto = new CompareSetDto();
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
            CompareSet compareSet = compareItemQueryService.selectCompareSet(id);
            List<CompareProductDto>
                    products =
                    compareItemQueryService.selectCompareItems(compareSet.getId()).stream().map(
                            compareItemCommandService::convertProduct2Dto).toList();
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
                    compareItemQueryService.selectSaveProducts(tokenInfo.get().getId()).stream().map(productService::convert2ListDto).toList();
            res.setData(Optional.of(products));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/products")
    public ResponseEntity<CustomResponse<List<CompareProductDto>>> compareProductList(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                                      @RequestParam(value = "productIdStr") String productIdStr) {
        CustomResponse<List<CompareProductDto>> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ALLOW), auth);
        try {
            Integer userId = null;
            if (tokenInfo != null && tokenInfo.isPresent() && tokenInfo.get().getType().equals(TokenAuthType.USER))
                userId = tokenInfo.get().getId();
            List<Integer> productIds = utils.str2IntList(productIdStr);
            if (productIds.size() != 3 && productIds.size() != 2)
                return res.throwError("비교하기는 2~3개의 상품만 가능합니다.", "INPUT_CHECK_REQUIRED");
            List<Product> products = productService.selectProductListWithIds(productIds);
            if (products.stream().map(Product::getCategoryId).map(v -> {
                Category category = categoryQueryService.findById(v);
                return category.getCategoryId();
            }).collect(Collectors.toSet()).size() != 1)
                return res.throwError("같은 카테고리의 상품끼리 비교 가능합니다.", "INPUT_CHECK_REQUIRED");
            Optional<CompareSet>
                    compareSet =
                    userId != null ? compareSetRepository.selectHavingSet(userId, productIds) : Optional.empty();
            List<CompareProductDto>
                    productDtos =
                    products.stream().map(compareItemCommandService::convertProduct2Dto).toList();
            res.setData(Optional.of(productDtos));
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
            if (productIds.size() != 3 && productIds.size() != 2)
                return res.throwError("비교하기는 2~3개의 상품만 가능합니다.", "INPUT_CHECK_REQUIRED");
            List<Product> products = productService.selectProductListWithIds(productIds);
            if (products.stream().map(Product::getCategoryId).map(v -> {
                Category category = categoryQueryService.findById(v);
                return category.getCategoryId();
            }).collect(Collectors.toSet()).size() != 1)
                return res.throwError("같은 카테고리의 상품끼리 비교 가능합니다.", "INPUT_CHECK_REQUIRED");
            if (compareItemQueryService.checkExistProductSet(tokenInfo.get().getId(), productIds))
                return res.throwError("이미 저장된 조합입니다.", "NOT_ALLOWED");
            CompareSet compareSet = compareItemCommandService.addCompareSet(tokenInfo.get().getId(), productIds);
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
            Boolean result = compareItemCommandService.addSaveProduct(tokenInfo.get().getId(), data.getProductId());
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
                CompareSet compareSet = compareItemQueryService.selectCompareSet(setId);
                if (tokenInfo.get().getId() != compareSet.getUserId())
                    return res.throwError("삭제 권한이 없습니다.", "NOT_ALLOWED");
            }
            for (Integer setId : data.getCompareSetIds()) {
                compareItemCommandService.deleteCompareSet(setId);
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
                saveProducts.add(compareItemQueryService.selectSaveProduct(tokenInfo.get().getId(), productId));
            }
            compareItemCommandService.deleteSaveProduct(saveProducts);
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
