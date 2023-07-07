package com.matsinger.barofishserver.product;

import com.matsinger.barofishserver.category.Category;
import com.matsinger.barofishserver.category.CategoryService;
import com.matsinger.barofishserver.compare.filter.CompareFilterService;
import com.matsinger.barofishserver.data.curation.CurationService;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.product.filter.ProductFilterService;
import com.matsinger.barofishserver.product.filter.ProductFilterValue;
import com.matsinger.barofishserver.product.object.*;
import com.matsinger.barofishserver.searchFilter.SearchFilterService;
import com.matsinger.barofishserver.searchFilter.object.ProductSearchFilterMap;
import com.matsinger.barofishserver.store.object.Store;
import com.matsinger.barofishserver.store.StoreService;
import com.matsinger.barofishserver.utils.Common;
import com.matsinger.barofishserver.utils.CustomResponse;
import com.matsinger.barofishserver.utils.S3.S3Uploader;
import jakarta.persistence.criteria.Predicate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product")
public class ProductController {

    private final ProductService productService;
    private final StoreService storeService;
    private final CategoryService categoryService;
    private final CurationService curationService;
    private final CompareFilterService compareFilterService;
    private final ProductFilterService productFilterService;
    private final SearchFilterService searchFilterService;
    private final JwtService jwt;

    private final Common utils;

    private final S3Uploader s3;

    @GetMapping("/recent-view")
    public ResponseEntity<CustomResponse<List<ProductListDto>>> selectRecentViewList(@RequestParam(value = "ids") String ids) {
        CustomResponse<List<ProductListDto>> res = new CustomResponse<>();
        try {
            List<Integer> idList = utils.str2IntList(ids);
//            List<Product> products = productService.selectProductListWithIds(idList);
//            res.setData(Optional.of(products.stream().map(v -> v.convert2ListDto()).toList()));
            List<ProductListDto> productListDtos = new ArrayList<>();
            for (Integer id : idList) {
                productListDtos.add(productService.convert2ListDto(productService.selectProduct(id)));
            }
            res.setData(Optional.of(productListDtos));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/")
    public ResponseEntity<CustomResponse<Page<SimpleProductDto>>> selectProductList(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                                    @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                                                                    @RequestParam(value = "take", required = false, defaultValue = "10") Integer take,
                                                                                    @RequestParam(value = "orderby", defaultValue = "createdAt") ProductOrderBy orderBy,
                                                                                    @RequestParam(value = "orderType", defaultValue = "DESC") Sort.Direction sort,
                                                                                    @RequestParam(value = "partnerName", required = false) String partnerName,
                                                                                    @RequestParam(value = "title", required = false) String title,
                                                                                    @RequestParam(value = "state", required = false) String state,
                                                                                    @RequestParam(value = "category", required = false) String category,
                                                                                    @RequestParam(value = "partnerId", required = false) String partnerId,
                                                                                    @RequestParam(value = "categoryId", required = false) String categoryId,
                                                                                    @RequestParam(value = "createdAtS", required = false) Timestamp createdAtS,
                                                                                    @RequestParam(value = "createdAtE", required = false) Timestamp createdAtE) {
        CustomResponse<Page<SimpleProductDto>> res = new CustomResponse<>();
        Optional<TokenInfo>
                tokenInfo =
                jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN, TokenAuthType.PARTNER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Specification<Product> spec = (root, query, builder) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (partnerName != null) predicates.add(builder.like(root.get("store").get("storeInfo").get("name"),
                        "%" + partnerName + "%"));
                if (title != null) predicates.add(builder.like(root.get("title"), "%" + title + "%"));
                if (category != null)
                    predicates.add(builder.like(root.get("category").get("name"), "%" + category + "%"));
                if (partnerId != null)
                    predicates.add(builder.and(root.get("store").get("id").in(Arrays.stream(partnerId.split(",")).map(
                            Integer::valueOf).toList())));
                if (state != null)
                    predicates.add(builder.and(root.get("state").in(Arrays.stream(state.split(",")).map(ProductState::valueOf).toList())));
                if (categoryId != null)
                    predicates.add(builder.and(root.get("category").get("id").in(Arrays.stream(categoryId.split(",")).map(
                            Integer::valueOf).toList())));
                if (createdAtS != null) predicates.add(builder.greaterThan(root.get("createdAt"), createdAtS));
                if (createdAtE != null) predicates.add(builder.lessThan(root.get("createdAt"), createdAtE));
                if (tokenInfo.get().getType().equals(TokenAuthType.PARTNER))
                    predicates.add(builder.equal(root.get("storeId"), tokenInfo.get().getId()));
                predicates.add(builder.notEqual(root.get("state"), ProductState.DELETED));
                return builder.and(predicates.toArray(new Predicate[0]));
            };
            Pageable pageRequest = PageRequest.of(page, take, Sort.by(sort, orderBy.label));
            Page<Product> products;
            products = productService.selectProductByAdmin(pageRequest, spec);

            Page<SimpleProductDto>
                    productDtos =
                    products.map(product -> productService.convert2SimpleDto(product, null));

            res.setData(Optional.of(productDtos));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/list/count")
    public ResponseEntity<CustomResponse<Long>> selectProductCountByUser(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                                                         @RequestParam(value = "take", defaultValue = "10") Integer take,
                                                                         @RequestParam(value = "sortby", defaultValue = "RECOMMEND", required = false) ProductSortBy sortBy,
                                                                         @RequestParam(value = "categoryIds", required = false) String categoryIds,
                                                                         @RequestParam(value = "filterFieldIds", required = false) String filterFieldIds,
                                                                         @RequestParam(value = "typeIds", required = false) String typeIds,
                                                                         @RequestParam(value = "locationIds", required = false) String locationIds,
                                                                         @RequestParam(value = "processIds", required = false) String processIds,
                                                                         @RequestParam(value = "usageIds", required = false) String usageIds,
                                                                         @RequestParam(value = "storageIds", required = false) String storageIds,
                                                                         @RequestParam(value = "curationId", required = false) Integer curationId,
                                                                         @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                                                                         @RequestParam(value = "storeId", required = false) Integer storeId) {
        CustomResponse<Long> res = new CustomResponse<>();
        try {
            Page<ProductListDto>
                    result =
                    productService.selectProductListWithPagination(page - 1,
                            take,
                            sortBy,
                            utils.str2IntList(categoryIds),
                            utils.str2IntList(filterFieldIds),
                            curationId,
                            keyword,
                            storeId);
            res.setData(Optional.of(result.getTotalElements()));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<CustomResponse<Page<ProductListDto>>> selectProductListByUser(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                                                                        @RequestParam(value = "take", defaultValue = "10") Integer take,
                                                                                        @RequestParam(value = "sortby", defaultValue = "RECOMMEND", required = false) ProductSortBy sortBy,
                                                                                        @RequestParam(value = "categoryIds", required = false) String categoryIds,
                                                                                        @RequestParam(value = "filterFieldIds", required = false) String filterFieldIds,
                                                                                        @RequestParam(value = "typeIds", required = false) String typeIds,
                                                                                        @RequestParam(value = "locationIds", required = false) String locationIds,
                                                                                        @RequestParam(value = "processIds", required = false) String processIds,
                                                                                        @RequestParam(value = "usageIds", required = false) String usageIds,
                                                                                        @RequestParam(value = "storageIds", required = false) String storageIds,
                                                                                        @RequestParam(value = "curationId", required = false) Integer curationId,
                                                                                        @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                                                                                        @RequestParam(value = "storeId", required = false) Integer storeId) {
        CustomResponse<Page<ProductListDto>> res = new CustomResponse<>();
        try {
            Page<ProductListDto>
                    result =
                    productService.selectProductListWithPagination(page - 1,
                            take,
                            sortBy,
                            utils.str2IntList(categoryIds),
                            utils.str2IntList(filterFieldIds),
                            curationId,
                            keyword,
                            storeId);
            res.setData(Optional.ofNullable(result));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomResponse<SimpleProductDto>> selectProduct(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                          @PathVariable("id") Integer id) {
        Optional<TokenInfo>
                tokenInfo =
                auth.isPresent() ? jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER,
                        TokenAuthType.PARTNER,
                        TokenAuthType.ADMIN), auth) : Optional.empty();
        CustomResponse<SimpleProductDto> res = new CustomResponse<>();
        try {
            Product product = productService.selectProduct(id);
            SimpleProductDto
                    productDto =
                    productService.convert2SimpleDto(product,
                            tokenInfo != null &&
                                    tokenInfo.isPresent() &&
                                    tokenInfo.get().getType().equals(TokenAuthType.USER) ? tokenInfo.get().getId() : null);
            res.setData(Optional.ofNullable(productDto));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/{id}/option")
    public ResponseEntity<CustomResponse<List<OptionDto>>> selectProductOptionList(@PathVariable("id") Integer id) {
        CustomResponse<List<OptionDto>> res = new CustomResponse<>();
        try {
            List<OptionDto> optionDtos = productService.selectProductOption(id);
            res.setData(Optional.ofNullable(optionDtos));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    private static class OptionItemAddReq {
        Boolean isRepresent;
        String name;
        Integer discountPrice;
        Integer amount;
        Integer purchasePrice;
        Integer originPrice;
        Integer deliveryFee;
        Integer deliverBoxPerAmount;
        Integer maxAvailableAmount;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    private static class OptionAddReq {
        Boolean isNeeded;
        List<OptionItemAddReq> items;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    private static class ProductFilterValueReq {
        Integer compareFilterId;
        String value;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    private static class ProductAddReq {
        Integer storeId;
        Integer categoryId;
        String title;
        Boolean isActive;
        String deliveryInfo;
        Integer deliveryFee;
        Integer expectedDeliverDay;
        Integer deliverBoxPerAmount;
        String descriptionContent;
        Boolean needTaxation;
        List<Integer> searchFilterFieldIds;
        List<OptionAddReq> options;
        List<ProductFilterValueReq> filterValues;
    }

    @PostMapping("/add")
    public ResponseEntity<CustomResponse<SimpleProductDto>> addProduct(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                       @RequestPart(value = "data") ProductAddReq data,
                                                                       @RequestPart(value = "images") List<MultipartFile> images) {
        CustomResponse<SimpleProductDto> res = new CustomResponse<>();
        Optional<TokenInfo>
                tokenInfo =
                jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN, TokenAuthType.PARTNER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            if (tokenInfo.get().getType().equals(TokenAuthType.ADMIN) && data.getStoreId() == null)
                return res.throwError("상점 아이디를 입력해주세요.", "INPUT_CHECK_REQUIRED");
            else if (tokenInfo.get().getType().equals(TokenAuthType.PARTNER)) data.setStoreId(tokenInfo.get().getId());
            Optional<Store> store = storeService.selectStoreOptional(data.getStoreId());
            if (store.isEmpty()) return res.throwError("가게 정보를 찾을 수 없습니다.", "NO_SUCH_DATA");
            Category category = categoryService.findById(data.getCategoryId());
            if (images.size() == 0) return res.throwError("이미지를 입력해주세요.", "INPUT_CHECK_REQUIRED");
            for (MultipartFile image : images) {
                if (!s3.validateImageType(image)) return res.throwError("허용되지 않는 확장자입니다.", "INPUT_CHECK_REQUIRED");
            }
            if (data.getDescriptionContent() == null) return res.throwError("상품 설명을 입력해주세요.", "INPUT_CHECK_REQUIRED");
//            if (data.getExpectedDeliverDay() == null) return res.throwError("도착 예정일을 입력해주세요.", "INPUT_CHECK_REQUIRED");
            String title = utils.validateString(data.getTitle(), 100L, "상품");
            data.getSearchFilterFieldIds().forEach(searchFilterService::selectSearchFilterField);
            String
                    deliveryInfo =
                    data.deliveryInfo != null ? utils.validateString(data.getDeliveryInfo(), 500L, "배송안내") : null;
            boolean existRepresent = false;
            if (data.getDeliveryFee() == null) data.setDeliveryFee(0);
            if (data.getOptions() == null || data.getOptions().size() == 0)
                return res.throwError("옵션은 최소 1개 이상 필수입니다.", "INPUT_CHECK_REQUIRED");
            if (data.getOptions().stream().noneMatch(v -> v.isNeeded))
                return res.throwError("필수 옵션은 최소 1개 이상입니다.", "INPUT_CHECK_REQUIRED");
            for (OptionAddReq optionData : data.getOptions()) {
                if (optionData.isNeeded == null) return res.throwError("필수 여부를 체크해주세요.", "INPUT_CHECK_REQUIRED");
                if (optionData.getItems() == null || optionData.getItems().size() == 0)
                    return res.throwError("옵션 아이템을 입력해주세요.", "INPUT_CHECK_REQUIRED");
                for (OptionItemAddReq itemData : optionData.getItems()) {
                    String name = utils.validateString(itemData.name, 100L, "옵션 이름");
                    itemData.setName(name);
                    if (itemData.isRepresent != null && itemData.isRepresent) existRepresent = true;
                    if (itemData.discountPrice == null) itemData.setDiscountPrice(0);
//                        return res.throwError("할인(판매) 가격을 입력해주세요.", "INPUT_CHECK_REQUIRED");
                    if (itemData.amount == null) itemData.setAmount(null);
//                        return res.throwError("개수를 입력해주세요.", "INPUT_CHECK_REQUIRED");
                    if (itemData.purchasePrice == null) itemData.setPurchasePrice(0);
//                        return res.throwError("매입가를 입력해주세요.", "INPUT_CHECK_REQUIRED");
                    if (!optionData.isNeeded && itemData.originPrice == null) itemData.setOriginPrice(0);
                    if (itemData.deliveryFee == null) itemData.setDeliveryFee(0);
//                    return res.throwError("배송비를 입력해주세요.", "INPUT_CHECK_REQUIRED");
                }
            }
            if (!existRepresent) return res.throwError("대표 옵션 아이템을 선택해주세요.", "INPUT_CHECK_REQUIRED");
            List<ProductFilterValue>
                    filterValues =
                    data.filterValues != null ? data.getFilterValues().stream().map(v -> {
                        try {
                            String valueName = utils.validateString(v.value, 50L, "필터 값");
                            compareFilterService.selectCompareFilter(v.compareFilterId);
                            return ProductFilterValue.builder().compareFilterId(v.compareFilterId).value(valueName).build();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }).toList() : null;

            //Setter
            Product product = new Product();
            product.setDiscountRate(0);
            product.setTitle(title);
            product.setOriginPrice(0);
            product.setCategory(category);
            product.setStoreId(data.getStoreId());
            product.setExpectedDeliverDay(data.getExpectedDeliverDay() != null ? data.getExpectedDeliverDay() : 0);
            product.setDeliveryInfo(deliveryInfo != null ? deliveryInfo : "");
            product.setImages("");
            product.setPointRate(0.0F);
            product.setDescriptionImages("");
            product.setState(ProductState.ACTIVE);
            product.setRepresentOptionItemId(null);
            product.setNeedTaxation(data.needTaxation != null ? data.needTaxation : true);
            product.setDeliverBoxPerAmount(data.deliverBoxPerAmount);
            product.setDeliveryFee(data.getDeliveryFee() != null ? data.getDeliveryFee() : 0);
            product.setState(!data.isActive ? ProductState.INACTIVE : ProductState.ACTIVE);
            product.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            Product result = productService.addProduct(product);
            OptionItem representOptionItem = null;
            if (data.getOptions() != null) {
                for (OptionAddReq od : data.getOptions()) {
                    Option
                            option =
                            Option.builder().productId(result.getId()).state(OptionState.ACTIVE).isNeeded(od.getIsNeeded()).description(
                                    "").build();
                    option = productService.addOption(option);
                    for (OptionItemAddReq itemData : od.getItems()) {
                        OptionItem
                                item =
                                OptionItem.builder().optionId(option.getId()).name(itemData.getName()).discountPrice(
                                        itemData.discountPrice).amount(itemData.getAmount()).purchasePrice(itemData.purchasePrice).originPrice(
                                        itemData.originPrice).state(OptionItemState.ACTIVE).deliverFee(itemData.deliveryFee).deliverBoxPerAmount(
                                        itemData.deliverBoxPerAmount).maxAvailableAmount(itemData.maxAvailableAmount).build();
                        item = productService.addOptionItem(item);
                        if (itemData.isRepresent != null && itemData.isRepresent) representOptionItem = item;
                    }
                }
            }
            if (filterValues != null)
                productFilterService.addAllProductFilter(filterValues.stream().peek(v -> v.setProductId(product.getId())).toList());
            List<String>
                    imagesUrl =
                    s3.uploadFiles(images, new ArrayList<>(Arrays.asList("product", String.valueOf(result.getId()))));
            String
                    descriptionContent =
                    s3.uploadEditorStringToS3(data.getDescriptionContent(),
                            new ArrayList<>(Arrays.asList("product", String.valueOf(result.getId()))));
            productService.addProductSearchFilters(data.searchFilterFieldIds.stream().map(v -> ProductSearchFilterMap.builder().productId(
                    product.getId()).fieldId(v).build()).toList());
            result.setImages(imagesUrl.toString());
            result.setDescriptionImages(descriptionContent);
            result.setRepresentOptionItemId(representOptionItem.getId());
            Product finalResult = productService.update(result.getId(), result);
            SimpleProductDto dto = productService.convert2SimpleDto(finalResult, null);
            dto.setReviews(null);
            res.setData(Optional.of(dto));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    private static class OptionItemUpdateReq {
        Boolean isRepresent;
        String name;
        Integer discountPrice;
        Integer amount;
        Integer purchasePrice;
        Integer originPrice;
        Integer deliveryFee;
        Integer deliverBoxPerAmount;
        Integer maxAvailableAmount;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    private static class OptionUpdateReq {
        Boolean isNeeded;
        List<Common.CudInput<OptionItemUpdateReq, Integer>> items;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    private static class ProductUpdateReq {
        Integer storeId;
        Integer categoryId;
        String title;
        Boolean isActive;

        String deliveryInfo;
        Integer deliveryFee;
        Integer expectedDeliverDay;
        Integer deliverBoxPerAmount;
        Boolean needTaxation;
        List<Integer> searchFilterFieldIds;
        String descriptionContent;
        List<ProductFilterValueReq> filterValues;
        List<Common.CudInput<OptionUpdateReq, Integer>> options;
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<CustomResponse<SimpleProductDto>> updateProduct(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                          @PathVariable(value = "id") Integer id,
                                                                          @RequestPart(value = "data") ProductUpdateReq data,
                                                                          @RequestPart(value = "existingImages", required = false) List<String> existingImages,
                                                                          @RequestPart(value = "newImages", required = false) List<MultipartFile> newImages) {
        CustomResponse<SimpleProductDto> res = new CustomResponse<>();
        Optional<TokenInfo>
                tokenInfo =
                jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN, TokenAuthType.PARTNER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            if (tokenInfo.get().getType().equals(TokenAuthType.ADMIN) && data.getStoreId() == null)
                return res.throwError("상점 아이디를 입력해주세요.", "INPUT_CHECK_REQUIRED");
            else if (tokenInfo.get().getType().equals(TokenAuthType.PARTNER)) data.setStoreId(tokenInfo.get().getId());
            Product product = productService.selectProduct(id);
            if (tokenInfo.get().getType().equals(TokenAuthType.PARTNER) &&
                    product.getStoreId() != tokenInfo.get().getId())
                return res.throwError("타지점의 상품입니다.", "UNAUTHORIZED");
            if (data.categoryId != null) {
                Category category = categoryService.findById(data.categoryId);
                product.setCategory(category);
            }
            if (newImages != null) {
                for (MultipartFile image : newImages) {
                    if (image != null && !s3.validateImageType(image))
                        return res.throwError("허용되지 않는 확장자입니다.", "INPUT_CHECK_REQUIRED");
                }
            }
            if (data.title != null) {
                String title = utils.validateString(data.title, 100L, "제목");
                product.setTitle(title);
            }
            if (data.isActive != null) {
                product.setState(data.isActive ? ProductState.ACTIVE : ProductState.INACTIVE);
            }
            if (data.needTaxation != null) {
                product.setNeedTaxation(data.needTaxation);
            }
            if (data.deliverBoxPerAmount != null) {
                product.setDeliverBoxPerAmount(data.deliverBoxPerAmount);
            }

            if (data.getDeliveryInfo() != null) {
                String deliveryInfo = utils.validateString(data.getDeliveryInfo(), 500L, "배송 안내");
                product.setDeliveryInfo(deliveryInfo);
            }
            if (data.getDeliveryFee() != null) {
                if (data.getDeliveryFee() < 0) return res.throwError("배달료를 확인해주세요.", "INPUT_CHECK_REQUIRED");
                product.setDeliveryFee(data.getDeliveryFee());
            }
            if (data.getExpectedDeliverDay() != null) {
                if (data.getExpectedDeliverDay() < 0) return res.throwError("예상 도착일을 입력해주세요.", "INPUT_CHECK_REQUIRED");
                product.setExpectedDeliverDay(data.getExpectedDeliverDay());
            }
            if (data.searchFilterFieldIds != null)
                data.getSearchFilterFieldIds().forEach(searchFilterService::selectSearchFilterField);
            if (data.getDescriptionContent() != null) {
                String
                        url =
                        s3.uploadEditorStringToS3(data.getDescriptionContent(),
                                new ArrayList<>(Arrays.asList("product", id.toString())));
                product.setDescriptionImages(url);
            }
            if (existingImages != null || newImages != null) {
                List<String> imgUrls = existingImages;
                if (newImages != null) existingImages.addAll(s3.uploadFiles(newImages,
                        new ArrayList<>(Arrays.asList("product", String.valueOf(id)))));

                product.setImages(imgUrls.toString());
            }
            List<ProductFilterValue>
                    filterValues =
                    data.filterValues != null ? data.getFilterValues().stream().map(v -> {
                        try {
                            String valueName = utils.validateString(v.value, 50L, "필터 값");
                            compareFilterService.selectCompareFilter(v.compareFilterId);
                            return ProductFilterValue.builder().productId(product.getId()).compareFilterId(v.compareFilterId).value(
                                    valueName).build();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }).toList() : null;
            OptionItem representOptionItem = null;
            if (data.getOptions() != null) {
                for (Common.CudInput<OptionUpdateReq, Integer> optionData : data.getOptions()) {
                    if (optionData.getType().equals(Common.CudType.CREATE)) {
                        if (optionData.getData() == null)
                            return res.throwError("CREATE의 경우 DATA는 필수 입니다.", "INPUT_CHECK_REQUIRED");
                        if (optionData.getData().getIsNeeded() == null)
                            return res.throwError("필수 여부를 입력해주세요.", "INPUT_CHECK_REQUIRED");
                        for (OptionItemUpdateReq itemData : optionData.getData().getItems().stream().map(Common.CudInput::getData).toList()) {
                            String name = utils.validateString(itemData.name, 100L, "옵션 이름");
                            itemData.setName(name);
                            if (itemData.discountPrice == null) itemData.setDiscountPrice(0);
//                                return res.throwError("할인(판매) 가격을 입력해주세요.", "INPUT_CHECK_REQUIRED");
                            if (itemData.amount == null) return res.throwError("개수를 입력해주세요.", "INPUT_CHECK_REQUIRED");
                            if (itemData.purchasePrice == null) itemData.setPurchasePrice(0);
//                                return res.throwError("매입가를 입력해주세요.", "INPUT_CHECK_REQUIRED");
                            if (itemData.originPrice == null) itemData.setOriginPrice(0);
                            if (itemData.deliveryFee == null)
                                return res.throwError("배송비를 입력해주세요.", "INPUT_CHECK_REQUIRED");
                        }
                    } else if (optionData.getType().equals(Common.CudType.UPDATE)) {
                        if (optionData.getId() == null || optionData.getData() == null)
                            return res.throwError("UPDATE인 경우 ID, DATA는 필수 입니다.", "INPUT_CHECK_REQUIRED");
                        Option option = productService.selectOption(optionData.getId());
                        for (Common.CudInput<OptionItemUpdateReq, Integer> itemData : optionData.getData().getItems()) {
                            if (itemData.getType().equals(Common.CudType.CREATE)) {
                                String name = utils.validateString(itemData.getData().name, 100L, "옵션 이름");
                                itemData.getData().setName(name);
                                if (itemData.getData().discountPrice == null)
                                    return res.throwError("할인(판매) 가격을 입력해주세요.", "INPUT_CHECK_REQUIRED");
                                if (itemData.getData().amount == null)
                                    return res.throwError("개수를 입력해주세요.", "INPUT_CHECK_REQUIRED");
                                if (itemData.getData().purchasePrice == null)
                                    return res.throwError("매입가를 입력해주세요.", "INPUT_CHECK_REQUIRED");
                                if (itemData.getData().originPrice == null) itemData.getData().setOriginPrice(0);
                                if (itemData.getData().deliveryFee == null)
                                    return res.throwError("배송비를 입력해주세요.", "INPUT_CHECK_REQUIRED");
                            } else if (itemData.getType().equals(Common.CudType.UPDATE)) {
                                if (itemData.getId() == null)
                                    return res.throwError("옵션 아이템 UPDATE 시 ID를 필수로 입력해주세요.", "INPUT_CHECK_REQUIRED");
                                OptionItem optionItem = productService.selectOptionItem(itemData.getId());
                            }
                        }
                    } else {
                        if (optionData.getId() == null)
                            return res.throwError("DELETE인 경우 ID는 필수 입니다.", "INPUT_CHECK_REQUIRED");
                        Option option = productService.selectOption(optionData.getId());
                    }
                }
            }
            Product result = productService.update(id, product);

            if (data.getOptions() != null) {
                for (Common.CudInput<OptionUpdateReq, Integer> optionData : data.getOptions()) {
                    if (optionData.getType().equals(Common.CudType.CREATE)) {
                        Option
                                option =
                                productService.addOption(Option.builder().productId(product.getId()).description("").state(OptionState.ACTIVE).isNeeded(
                                        optionData.getData().getIsNeeded()).build());
                        for (OptionItemUpdateReq itemData : optionData.getData().getItems().stream().map(Common.CudInput::getData).toList()) {
                            OptionItem
                                    optionItem =
                                    productService.addOptionItem(OptionItem.builder().optionId(option.getId()).name(
                                            itemData.name).discountPrice(itemData.discountPrice).amount(itemData.amount).purchasePrice(
                                            itemData.purchasePrice).originPrice(itemData.originPrice).state(OptionItemState.ACTIVE).deliverFee(
                                            itemData.deliveryFee).deliverBoxPerAmount(itemData.deliverBoxPerAmount).maxAvailableAmount(
                                            itemData.maxAvailableAmount).build());
                            if (itemData.isRepresent != null && itemData.isRepresent) representOptionItem = optionItem;
                        }
                    } else if (optionData.getType().equals(Common.CudType.UPDATE)) {
                        Option option = productService.selectOption(optionData.getId());
                        if (optionData.getData().isNeeded != null) option.setIsNeeded(optionData.getData().isNeeded);
                        for (Common.CudInput<OptionItemUpdateReq, Integer> itemData : optionData.getData().items) {
                            OptionItemUpdateReq d = itemData.getData();
                            if (itemData.getType().equals(Common.CudType.CREATE)) {
                                OptionItem
                                        optionItem =
                                        productService.addOptionItem(OptionItem.builder().optionId(option.getId()).name(
                                                d.getName()).discountPrice(d.discountPrice).state(OptionItemState.ACTIVE).amount(d.amount).purchasePrice(
                                                d.purchasePrice).originPrice(d.originPrice).deliverFee(d.deliveryFee).deliverBoxPerAmount(
                                                d.deliverBoxPerAmount).maxAvailableAmount(d.maxAvailableAmount).build());
                                if (d.isRepresent != null && d.isRepresent) representOptionItem = optionItem;
                            } else if (itemData.getType().equals(Common.CudType.UPDATE)) {
                                System.out.println("123");
                                OptionItem
                                        optionItem =
                                        OptionItem.builder().id(itemData.getId()).optionId(option.getId()).name(itemData.getData().name).discountPrice(
                                                itemData.getData().discountPrice).amount(itemData.getData().amount).state(OptionItemState.ACTIVE).purchasePrice(
                                                itemData.getData().purchasePrice).originPrice(itemData.getData().originPrice).deliverFee(
                                                itemData.getData().deliveryFee).deliverBoxPerAmount(itemData.getData().deliverBoxPerAmount).maxAvailableAmount(
                                                itemData.getData().maxAvailableAmount).build();
                                System.out.println("123");
                                optionItem = productService.addOptionItem(optionItem);
                                if (d.isRepresent != null && d.isRepresent) representOptionItem = optionItem;
                            } else {
                                productService.deleteOptionItem(itemData.getId());
                            }
                        }
                    } else {
                        productService.deleteOption(optionData.getId());
                    }
                }
            }
            if (data.searchFilterFieldIds != null) {
                productService.deleteProductSearchFilters(product.getId());
                productService.addProductSearchFilters(data.searchFilterFieldIds.stream().map(v -> ProductSearchFilterMap.builder().productId(
                        product.getId()).fieldId(v).build()).toList());
            }
            if (representOptionItem != null) {
                product.setRepresentOptionItemId(representOptionItem.getId());
                productService.update(product.getId(), product);
            }
            if (filterValues != null) {
                productFilterService.deleteAllFilterValueWithProductId(product.getId());
                productFilterService.addAllProductFilter(filterValues);
            }
            res.setData(Optional.ofNullable(productService.convert2SimpleDto(result, null)));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }


    @PostMapping("/like")
    public ResponseEntity<CustomResponse<Boolean>> likeProductByUser(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                     @RequestParam(value = "productId") Integer productId,
                                                                     @RequestParam(value = "type") LikePostType type) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Integer check = productService.checkLikeProduct(productId, tokenInfo.get().getId());
            if (check == 0 && type.equals(LikePostType.LIKE)) {
                productService.likeProduct(productId, tokenInfo.get().getId());
                res.setData(Optional.of(true));
            } else if (check == 1 && type.equals(LikePostType.UNLIKE)) {
                productService.unlikeProduct(productId, tokenInfo.get().getId());
                res.setData(Optional.of(true));
            }
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse<Boolean>> deleteProduct(@PathVariable("id") Integer id,
                                                                 @RequestHeader(value = "Authorization") Optional<String> auth) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo>
                tokenInfo =
                jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.PARTNER, TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Product product = productService.selectProduct(id);
            if (tokenInfo.get().getType().equals(TokenAuthType.PARTNER) &&
                    product.getStoreId() != tokenInfo.get().getId())
                return res.throwError("삭제 권한이 없습니다.", "NOT_ALLOWED");
            curationService.deleteWithProductId(product.getId());
            product.setState(ProductState.DELETED);
            product = productService.update(product.getId(), product);
            res.setData(Optional.of(true));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }
}
