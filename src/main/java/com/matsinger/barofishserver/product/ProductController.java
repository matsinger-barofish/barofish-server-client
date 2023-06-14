package com.matsinger.barofishserver.product;

import com.matsinger.barofishserver.category.Category;
import com.matsinger.barofishserver.category.CategoryService;
import com.matsinger.barofishserver.inquiry.InquiryService;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.product.object.*;
import com.matsinger.barofishserver.product.productinfo.*;
import com.matsinger.barofishserver.review.ReviewService;
import com.matsinger.barofishserver.store.object.Store;
import com.matsinger.barofishserver.store.StoreService;
import com.matsinger.barofishserver.utils.Common;
import com.matsinger.barofishserver.utils.CustomResponse;
import com.matsinger.barofishserver.utils.S3.S3Uploader;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
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
    private final ProductInfoService productInfoService;
    private final ReviewService reviewService;
    private final InquiryService inquiryService;
    private final JwtService jwt;

    private final Common utils;

    private final S3Uploader s3;

    @GetMapping("/")
    public ResponseEntity<CustomResponse<List<SimpleProductDto>>> selectProductList(@RequestHeader(value = "Authorization") Optional<String> auth) {
        CustomResponse<List<SimpleProductDto>> res = new CustomResponse<>();
        Optional<TokenInfo>
                tokenInfo =
                jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN, TokenAuthType.PARTNER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            List<Product> products = new ArrayList<>();
            if (tokenInfo.get().getType().equals(TokenAuthType.ADMIN)) products = productService.selectProductByAdmin();
            else products = productService.selectProductListByPartner(tokenInfo.get().getId());
            List<SimpleProductDto> productDtos = new ArrayList<>();
            for (Product product : products) {
                productDtos.add(productService.convert2SimpleDto(product, null));
            }
            res.setData(Optional.ofNullable(productDtos));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<CustomResponse<List<ProductListDto>>> selectProductListByUser(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                                                                        @RequestParam(value = "take", defaultValue = "10") Integer take,
                                                                                        @RequestParam(value = "sortby", defaultValue = "RECOMMEND", required = false) ProductSortBy sortBy,
                                                                                        @RequestParam(value = "categoryIds", required = false) String categoryIds,
                                                                                        @RequestParam(value = "typeIds", required = false) String typeIds,
                                                                                        @RequestParam(value = "locationIds", required = false) String locationIds,
                                                                                        @RequestParam(value = "processIds", required = false) String processIds,
                                                                                        @RequestParam(value = "usageIds", required = false) String usageIds,
                                                                                        @RequestParam(value = "storageIds", required = false) String storageIds) {
        CustomResponse<List<ProductListDto>> res = new CustomResponse<>();
        try {
            List<ProductListDto>
                    result =
                    productService.selectProductListWithPagination(page - 1,
                            take,
                            sortBy,
                            utils.str2IntList(categoryIds),
                            utils.str2IntList(typeIds),
                            utils.str2IntList(locationIds),
                            utils.str2IntList(processIds),
                            utils.str2IntList(usageIds),
                            utils.str2IntList(storageIds));
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
        String name;
        Integer discountRate;
        Integer price;
        Integer amount;
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
    private static class ProductAddReq {
        Integer storeId;
        Integer categoryId;
        String title;
        Integer originPrice;
        Integer discountRate;
        String deliveryInfo;
        Integer deliveryFee;
        Integer expectedDeliverDay;
        Integer typeId;
        Integer locationId;
        Integer usageId;
        Integer storageId;
        Integer processId;
        String descriptionContent;
        List<OptionAddReq> options;
    }

    @PostMapping("/add")
    public ResponseEntity<CustomResponse<Product>> addProduct(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                              @RequestPart(value = "data") ProductAddReq data,
                                                              @RequestPart(value = "images") List<MultipartFile> images) {
        CustomResponse<Product> res = new CustomResponse<>();
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

            if (data.getExpectedDeliverDay() == null) return res.throwError("도착 예정일을 입력해주세요.", "INPUT_CHECK_REQUIRED");
            String title = utils.validateString(data.getTitle(), 100L, "상품");
            if (data.getOriginPrice() <= 0) return res.throwError("금액을 확인해주세요.", "INPUT_CHECK_REQUIRED");
            if (data.getDiscountRate() != null && data.getDiscountRate() < 0)
                return res.throwError("할인률을 확인해주세요.", "INPUT_CHECK_REQUIRED");
            productInfoService.selectProductType(data.getTypeId());
            productInfoService.selectProductProcess(data.getProcessId());
            productInfoService.selectProductLocation(data.getLocationId());
            productInfoService.selectProductUsage(data.getUsageId());
            productInfoService.selectProductStorage(data.getStorageId());
            String deliveryInfo = utils.validateString(data.getDeliveryInfo(), 500L, "배송안내");
            if (data.getDeliveryFee() == null) data.setDeliveryFee(0);
            if (data.getOptions() != null) {
                for (OptionAddReq optionData : data.getOptions()) {
                    if (optionData.isNeeded == null) return res.throwError("필수 여부를 체크해주세요.", "INPUT_CHECK_REQUIRED");
                    if (optionData.getItems() == null) return res.throwError("옵션 아이템을 입력해주세요.", "INPUT_CHECK_REQUIRED");
                    for (OptionItemAddReq itemData : optionData.getItems()) {
                        String name = utils.validateString(itemData.getName(), 100L, "옵션 이름");
                        if (itemData.getPrice() == null) return res.throwError("가격을 입력해주세요.", "INPUT_CHECK_REQUIRED");
                        itemData.setName(name);
                    }
                }
            }

            //Setter
            Product product = new Product();
            product.setDiscountRate(data.getDiscountRate());
            product.setTitle(title);
            product.setOriginPrice(data.getOriginPrice());
            product.setCategory(category);
            product.setStoreId(data.getStoreId());
            product.setExpectedDeliverDay(data.getExpectedDeliverDay());
            product.setDeliveryInfo(deliveryInfo);
            product.setImages("");
            product.setDescriptionImages("");
            product.setState(ProductState.ACTIVE);
            product.setDeliveryFee(data.getDeliveryFee());
            product.setProductType(productInfoService.selectProductType(data.getTypeId()));
            product.setProductLocation(productInfoService.selectProductLocation(data.getLocationId()));
            product.setProductStorage(productInfoService.selectProductStorage(data.getStorageId()));
            product.setProductProcess(productInfoService.selectProductProcess(data.getProcessId()));
            product.setProductUsage(productInfoService.selectProductUsage(data.getUsageId()));
            product.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            Product result = productService.addProduct(product);
            if (data.getOptions() != null) {

                for (OptionAddReq od : data.getOptions()) {
                    Option
                            option =
                            Option.builder().productId(result.getId()).isNeeded(od.getIsNeeded()).description("").build();
                    option = productService.addOption(option);
                    for (OptionItemAddReq itemData : od.getItems()) {
                        OptionItem
                                item =
                                OptionItem.builder().optionId(option.getId()).name(itemData.getName()).discountRate(
                                        itemData.getDiscountRate()).price(itemData.getPrice()).amount(itemData.getAmount()).build();
                        productService.addOptionItem(item);
                    }
                }

            }

            List<String>
                    imagesUrl =
                    s3.uploadFiles(images, new ArrayList<>(Arrays.asList("product", String.valueOf(result.getId()))));
            String
                    descriptionContent =
                    s3.uploadEditorStringToS3(data.getDescriptionContent(),
                            new ArrayList<>(Arrays.asList("product", String.valueOf(result.getId()))));
            result.setImages(imagesUrl.toString());
            result.setDescriptionImages(descriptionContent);
            Product finalResult = productService.update(result.getId(), result);
            res.setData(Optional.ofNullable(finalResult));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    private static class ProductUpdateReq {
        Integer storeId;
        Integer categoryId;
        String title;
        Integer originPrice;
        Integer discountRate;
        String deliveryInfo;
        Integer deliveryFee;
        Integer expectedDeliverDay;
        Integer typeId;
        Integer locationId;
        Integer usageId;
        Integer storageId;
        Integer processId;
        String descriptionContent;
        List<OptionAddReq> options;
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<CustomResponse<Product>> updateProduct(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                 @PathVariable(value = "id") Integer id,
                                                                 @RequestPart(value = "data") ProductUpdateReq data,
                                                                 @RequestPart(value = "existingImages", required = false) List<String> existingImages,
                                                                 @RequestPart(value = "newImages", required = false) List<MultipartFile> newImages) {
        CustomResponse<Product> res = new CustomResponse<>();
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
            if (data.getOriginPrice() != null) {
                if (data.getOriginPrice() <= 0) return res.throwError("금액을 확인해주세요.", "INPUT_CHECK_REQUIRED");
                product.setOriginPrice(data.getOriginPrice());
            }
            if (data.getDiscountRate() != null) {
                if (data.getDiscountRate() < 0) return res.throwError("할인률을 확인해주세요.", "INPUT_CHECK_REQUIRED");
                product.setDiscountRate(data.getDiscountRate());
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
            if (data.getTypeId() != null) {
                ProductType type = productInfoService.selectProductType(data.getTypeId());
                product.setProductType(type);
            }
            if (data.getLocationId() != null) {
                ProductLocation location = productInfoService.selectProductLocation(data.getLocationId());
                product.setProductLocation(location);
            }
            if (data.getUsageId() != null) {
                ProductUsage usage = productInfoService.selectProductUsage(data.getUsageId());
                product.setProductUsage(usage);
            }
            if (data.getStorageId() != null) {
                ProductStorage storage = productInfoService.selectProductStorage(data.getStorageId());
                product.setProductStorage(storage);
            }
            if (data.getProcessId() != null) {
                ProductProcess process = productInfoService.selectProductProcess(data.getProcessId());
                product.setProductProcess(process);
            }
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
//                List<String>
//                        imgUrls =
//                        s3.processFileUpdateInput(images,
//                                new ArrayList<>(Arrays.asList("product", String.valueOf(id))));
//                product.setImages(imgUrls.toString());
            }
            if (data.getOptions() != null) {
                for (OptionAddReq optionData : data.getOptions()) {
                    if (optionData.isNeeded == null) return res.throwError("필수 여부를 체크해주세요.", "INPUT_CHECK_REQUIRED");
                    if (optionData.getItems() == null) return res.throwError("옵션 아이템을 입력해주세요.", "INPUT_CHECK_REQUIRED");
                    for (OptionItemAddReq itemData : optionData.getItems()) {
                        String name = utils.validateString(itemData.getName(), 100L, "옵션 이름");
                        if (itemData.getPrice() == null) return res.throwError("가격을 입력해주세요.", "INPUT_CHECK_REQUIRED");
                        itemData.setName(name);
                    }
                }
            }
            Product result = productService.update(id, product);

            if (data.getOptions() != null) {
                productService.deleteOptions(product.getId());
                for (OptionAddReq od : data.getOptions()) {
                    Option
                            option =
                            Option.builder().productId(result.getId()).isNeeded(od.getIsNeeded()).description("").build();
                    option = productService.addOption(option);
                    for (OptionItemAddReq itemData : od.getItems()) {
                        OptionItem
                                item =
                                OptionItem.builder().optionId(option.getId()).name(itemData.getName()).discountRate(
                                        itemData.getDiscountRate()).price(itemData.getPrice()).amount(itemData.getAmount()).build();
                        productService.addOptionItem(item);
                    }
                }
            }

            res.setData(Optional.ofNullable(result));
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
            product.setState(ProductState.DELETED);
            product = productService.update(product.getId(), product);
            res.setData(Optional.of(true));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }
}
