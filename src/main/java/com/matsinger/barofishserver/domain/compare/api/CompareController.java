package com.matsinger.barofishserver.domain.compare.api;

import com.matsinger.barofishserver.domain.category.application.CategoryQueryService;
import com.matsinger.barofishserver.domain.category.domain.Category;
import com.matsinger.barofishserver.domain.compare.application.CompareItemCommandService;
import com.matsinger.barofishserver.domain.compare.application.CompareItemQueryService;
import com.matsinger.barofishserver.domain.compare.domain.CompareSet;
import com.matsinger.barofishserver.domain.compare.domain.SaveProduct;
import com.matsinger.barofishserver.domain.compare.dto.*;
import com.matsinger.barofishserver.domain.compare.recommend.domain.RecommendCompareSet;
import com.matsinger.barofishserver.domain.compare.recommend.dto.RecommendCompareSetDto;
import com.matsinger.barofishserver.domain.compare.recommend.application.RecommendCompareSetService;
import com.matsinger.barofishserver.domain.compare.recommend.domain.RecommendCompareSetType;
import com.matsinger.barofishserver.domain.compare.repository.CompareSetRepository;
import com.matsinger.barofishserver.global.error.ErrorCode;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.domain.product.application.ProductService;
import com.matsinger.barofishserver.domain.product.domain.Product;
import com.matsinger.barofishserver.domain.product.dto.ProductListDto;
import com.matsinger.barofishserver.jwt.exception.JwtBusinessException;
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
    public ResponseEntity<CustomResponse<CompareMain>> selectMain(
            @RequestHeader(value = "Authorization") Optional<String> auth,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "take", required = false, defaultValue = "10") Integer take) {

        CustomResponse<CompareMain> res = new CustomResponse<>();

        if (auth.isEmpty()) {
            throw new JwtBusinessException(ErrorCode.TOKEN_REQUIRED);
        }
        TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth.get());
        Integer userId = tokenInfo.getId();

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
    }

    @GetMapping("/set/list")
    public ResponseEntity<CustomResponse<List<CompareSetDto>>> selectCompareSetList(
            @RequestHeader("Authorization") Optional<String> auth) {
        CustomResponse<List<CompareSetDto>> res = new CustomResponse<>();

        Integer userId = null;
        if (auth.isEmpty()) {
            throw new JwtBusinessException(ErrorCode.TOKEN_REQUIRED);
        }
        TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth.get());

        List<CompareSet> compareSets = compareItemQueryService.selectCompareSetList(tokenInfo.getId());
        List<CompareSetDto> compareSetDtos = compareSets.stream().map(compareSet -> {
            List<ProductListDto> products = compareItemQueryService.selectCompareItems(compareSet.getId()).stream()
                    .map(productService::convert2ListDto).toList();
            CompareSetDto compareSetDto = new CompareSetDto();
            compareSetDto.setCompareSetId(compareSet.getId());
            compareSetDto.setProducts(products);
            return compareSetDto;
        }).toList();
        res.setData(Optional.of(compareSetDtos));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/set/{id}")
    public ResponseEntity<CustomResponse<List<CompareProductDto>>> selectCompareSet(
            @RequestHeader("Authorization") Optional<String> auth,
            @PathVariable("id") Integer id) {
        CustomResponse<List<CompareProductDto>> res = new CustomResponse<>();

        if (auth.isEmpty()) {
            throw new JwtBusinessException(ErrorCode.TOKEN_REQUIRED);
        }
        TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth.get());

        CompareSet compareSet = compareItemQueryService.selectCompareSet(id);
        List<CompareProductDto> products = compareItemQueryService.selectCompareItems(compareSet.getId()).stream()
                .map(
                        compareItemCommandService::convertProduct2Dto)
                .toList();
        res.setData(Optional.of(products));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/save")
    public ResponseEntity<CustomResponse<List<ProductListDto>>> selectSaveProductList(
            @RequestHeader("Authorization") Optional<String> auth) {
        CustomResponse<List<ProductListDto>> res = new CustomResponse<>();

        if (auth.isEmpty()) {
            throw new JwtBusinessException(ErrorCode.TOKEN_REQUIRED);
        }
        TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth.get());

            List<ProductListDto> products = compareItemQueryService.selectSaveProducts(tokenInfo.getId()).stream()
                    .map(productService::convert2ListDto).toList();
            res.setData(Optional.of(products));
            return ResponseEntity.ok(res);
    }

    @GetMapping("/products")
    public ResponseEntity<CustomResponse<List<CompareProductDto>>> compareProductList(
            @RequestHeader(value = "Authorization") Optional<String> auth,
            @RequestParam(value = "productIdStr") String productIdStr) {
        CustomResponse<List<CompareProductDto>> res = new CustomResponse<>();

        if (auth.isEmpty()) {
            throw new JwtBusinessException(ErrorCode.TOKEN_REQUIRED);
        }
        TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth.get());
        Integer userId = tokenInfo.getId();

        List<Integer> productIds = utils.str2IntList(productIdStr);
        if (productIds.size() != 3 && productIds.size() != 2)
            throw new IllegalArgumentException("비교하기는 2~3개의 상품만 가능합니다.");
        List<Product> products = productService.selectProductListWithIds(productIds);
        if (products.stream().map(Product::getCategoryId).map(v -> {
            Category category = categoryQueryService.findById(v);
            return category.getCategoryId();
        }).collect(Collectors.toSet()).size() != 1)
            throw new IllegalArgumentException("같은 카테고리의 상품끼리 비교 가능합니다.");
        Optional<CompareSet> compareSet = compareSetRepository.selectHavingSet(userId, productIds);
        List<CompareProductDto> productDtos = products.stream()
                .map(compareItemCommandService::convertProduct2Dto).toList();
        res.setData(Optional.of(productDtos));
        return ResponseEntity.ok(res);
    }

    @PostMapping("/add-set")
    public ResponseEntity<CustomResponse<CompareSet>> addCompareSet(
            @RequestHeader("Authorization") Optional<String> auth,
            @RequestBody List<Integer> productIds) {
        CustomResponse<CompareSet> res = new CustomResponse<>();

        if (auth.isEmpty()) {
            throw new JwtBusinessException(ErrorCode.TOKEN_REQUIRED);
        }
        TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth.get());

        if (productIds.size() != 3 && productIds.size() != 2)
            throw new IllegalArgumentException("비교하기는 2~3개의 상품만 가능합니다.");
        List<Product> products = productService.selectProductListWithIds(productIds);
        if (products.stream().map(Product::getCategoryId).map(v -> {
            Category category = categoryQueryService.findById(v);
            return category.getCategoryId();
        }).collect(Collectors.toSet()).size() != 1)
            throw new IllegalArgumentException("같은 카테고리의 상품끼리 비교 가능합니다.");
        if (compareItemQueryService.checkExistProductSet(tokenInfo.getId(), productIds))
            throw new IllegalArgumentException("이미 저장된 조합입니다.");
        CompareSet compareSet = compareItemCommandService.addCompareSet(tokenInfo.getId(), productIds);
        res.setData(Optional.ofNullable(compareSet));
        return ResponseEntity.ok(res);
    }

    @PostMapping("/save-product")
    public ResponseEntity<CustomResponse<Boolean>> saveProduct(@RequestHeader("Authorization") Optional<String> auth,
            @RequestPart(value = "data") SaveProductReq data) {
        CustomResponse<Boolean> res = new CustomResponse<>();

        if (auth.isEmpty()) {
            throw new JwtBusinessException(ErrorCode.TOKEN_REQUIRED);
        }
        TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth.get());

        Boolean result = compareItemCommandService.addSaveProduct(tokenInfo.getId(), data.getProductId());
        res.setData(Optional.ofNullable(result));
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/set")
    public ResponseEntity<CustomResponse<Boolean>> deleteCompareSet(
            @RequestHeader("Authorization") Optional<String> auth,
            @RequestPart(value = "data") DeleteCompareSetReq data) {
        CustomResponse<Boolean> res = new CustomResponse<>();

        if (auth.isEmpty()) {
            throw new JwtBusinessException(ErrorCode.TOKEN_REQUIRED);
        }
        TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth.get());

        for (Integer setId : data.getCompareSetIds()) {
            CompareSet compareSet = compareItemQueryService.selectCompareSet(setId);
            if (tokenInfo.getId() != compareSet.getUserId())
                throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }
        for (Integer setId : data.getCompareSetIds()) {
            compareItemCommandService.deleteCompareSet(setId);
        }
        res.setData(Optional.of(true));
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/save")
    public ResponseEntity<CustomResponse<Boolean>> deleteSaveProducts(
            @RequestHeader("Authorization") Optional<String> auth,
            @RequestPart(value = "data") DeleteSaveProductReq data) {
        CustomResponse<Boolean> res = new CustomResponse<>();

        if (auth.isEmpty()) {
            throw new JwtBusinessException(ErrorCode.TOKEN_REQUIRED);
        }
        TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth.get());

        List<SaveProduct> saveProducts = new ArrayList<>();
        for (Integer productId : data.getProductIds()) {
            saveProducts.add(compareItemQueryService.selectSaveProduct(tokenInfo.getId(), productId));
        }
        compareItemCommandService.deleteSaveProduct(saveProducts);
        res.setData(Optional.of(true));
        return ResponseEntity.ok(res);
    }

    // ------추천 비교하기 세트-------------------------------
    @GetMapping("/recommend/list")
    public ResponseEntity<CustomResponse<List<RecommendCompareSetDto>>> selectRecommendCompareSetListByAdmin(
            @RequestHeader("Authorization") Optional<String> auth,
            @RequestParam(value = "type") RecommendCompareSetType type) {
        CustomResponse<List<RecommendCompareSetDto>> res = new CustomResponse<>();

        if (auth.isEmpty()) {
            throw new JwtBusinessException(ErrorCode.TOKEN_REQUIRED);
        }
        jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth.get());

        List<RecommendCompareSetDto> recommendCompareSetDtos = recommendCompareSetService
                .selectRecommendCompareSetList(type).stream().map(v -> recommendCompareSetService.convert2Dto(
                        v,
                        null))
                .toList();
        res.setData(Optional.of(recommendCompareSetDtos));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/recommend/{id}")
    public ResponseEntity<CustomResponse<RecommendCompareSetDto>> selectRecommendCompareSet(
            @PathVariable("id") Integer id) {
        CustomResponse<RecommendCompareSetDto> res = new CustomResponse<>();

        RecommendCompareSetDto recommendCompareSetDto = recommendCompareSetService.convert2Dto(
                recommendCompareSetService.selectRecommendCompareSet(id),
                null);
        res.setData(Optional.ofNullable(recommendCompareSetDto));
        return ResponseEntity.ok(res);
    }

    @PostMapping("/recommend/add")
    public ResponseEntity<CustomResponse<RecommendCompareSetDto>> addRecommendCompareSet(
            @RequestHeader("Authorization") Optional<String> auth,
            @RequestPart(value = "data") AddRecommendCompareSet data) {
        CustomResponse<RecommendCompareSetDto> res = new CustomResponse<>();

        if (auth.isEmpty()) {
            throw new JwtBusinessException(ErrorCode.TOKEN_REQUIRED);
        }
        jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth.get());

        if (data.getType() == null)
            throw new IllegalArgumentException("타입을 입력해주세요.");
        if (data.getProductIds() == null)
            throw new IllegalArgumentException("상품 아이디를 입력해주세요.");
        if (new HashSet<>(data.getProductIds()).size() != 3)
            throw new IllegalArgumentException("3개의 상품을 입력해주세요.");
        List<Product> products = data.getProductIds().stream().map(productService::selectProduct).toList();

        if (products.stream().map(v -> v.getCategory() != null ? v.getCategory().getCategoryId() : null)
                .collect(Collectors.toSet()).size() != 1)
            throw new IllegalArgumentException("같은 카테고리 내에서 선정 가능합니다.");

        RecommendCompareSet recommendCompareSet = recommendCompareSetService
                .addRecommendCompareSet(RecommendCompareSet.builder().type(data.getType()).product1Id(
                        data.getProductIds().get(0)).product2Id(data.getProductIds().get(1))
                        .product3Id(data.getProductIds().get(
                                2))
                        .build());
        res.setData(Optional.ofNullable(recommendCompareSetService.convert2Dto(recommendCompareSet, null)));
        return ResponseEntity.ok(res);
    }

    @PostMapping("/recommend/update/{id}")
    public ResponseEntity<CustomResponse<RecommendCompareSetDto>> updateRecommendCompareSet(
            @RequestHeader("Authorization") Optional<String> auth,
            @RequestPart(value = "data") AddRecommendCompareSet data,
            @PathVariable("id") Integer id) {
        CustomResponse<RecommendCompareSetDto> res = new CustomResponse<>();

        if (auth.isEmpty()) {
            throw new JwtBusinessException(ErrorCode.TOKEN_REQUIRED);
        }
        jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth.get());

        RecommendCompareSet set = recommendCompareSetService.selectRecommendCompareSet(id);
        if (data.getProductIds() == null)
            throw new IllegalArgumentException("상품 아이디를 입력해주세요.");
        if (data.getType() != null) {
            set.setType(data.getType());
        }
        if (new HashSet<>(data.getProductIds()).size() != 3)
            throw new IllegalArgumentException("3개의 상품을 입력해주세요.");

        List<Product> products = data.getProductIds().stream().map(productService::selectProduct).toList();

        if (products.stream().map(v -> v.getCategory() != null ? v.getCategory().getCategoryId() : null)
                .collect(Collectors.toSet()).size() != 1)
            throw new IllegalArgumentException("같은 카테고리 내에서 선정 가능합니다.");
        set.setProduct1Id(data.getProductIds().get(0));
        set.setProduct2Id(data.getProductIds().get(1));
        set.setProduct3Id(data.getProductIds().get(2));
        set = recommendCompareSetService.updateRecommendCompareSet(set);
        RecommendCompareSet recommendCompareSet = recommendCompareSetService.addRecommendCompareSet(set);
        res.setData(Optional.ofNullable(recommendCompareSetService.convert2Dto(recommendCompareSet, null)));
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/recommend/{id}")
    public ResponseEntity<CustomResponse<Boolean>> deleteRecommendCompareSet(
            @RequestHeader("Authorization") Optional<String> auth,
            @PathVariable("id") Integer id) {
        CustomResponse<Boolean> res = new CustomResponse<>();

        if (auth.isEmpty()) {
            throw new JwtBusinessException(ErrorCode.TOKEN_REQUIRED);
        }
        jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth.get());

        RecommendCompareSet set = recommendCompareSetService.selectRecommendCompareSet(id);
        recommendCompareSetService.deleteRecommendCompareSet(id);
        res.setData(Optional.of(true));
        return ResponseEntity.ok(res);
    }
}
