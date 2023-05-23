package com.matsinger.barofishserver.product;

import com.matsinger.barofishserver.category.Category;
import com.matsinger.barofishserver.category.CategoryService;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.store.Store;
import com.matsinger.barofishserver.store.StoreService;
import com.matsinger.barofishserver.utils.Common;
import com.matsinger.barofishserver.utils.CustomResponse;
import com.matsinger.barofishserver.utils.S3.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product")
public class ProductController {

    private final ProductService productService;
    private final StoreService storeService;
    private final CategoryService categoryService;
    private final JwtService jwt;

    private final Common utils;

    private final S3Uploader s3;

    @PostMapping("/test")
    public Boolean test(@RequestPart(value = "images", required = false) List<MultipartFile> images) {
        return true;
    }

    @PostMapping("/add")
    public ResponseEntity<CustomResponse<Product>> addProduct(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                              @RequestPart(value = "storeId", required = false) Integer storeId,
                                                              @RequestPart(value = "categoryId") Integer categoryId,
                                                              @RequestPart(value = "images") List<MultipartFile> images,
                                                              @RequestPart(value = "title") String title,
                                                              @RequestPart(value = "originPrice") Integer originPrice,
                                                              @RequestPart(value = "discountRate", required = false) Integer discountRate,
                                                              @RequestPart(value = "deliveryInfo") String deliveryInfo,
                                                              @RequestPart(value = "descriptionImages") List<MultipartFile> descriptionImages) {
        CustomResponse<Product> res = new CustomResponse<>();
        Optional<TokenInfo>
                tokenInfo =
                jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN, TokenAuthType.PARTNER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            if (tokenInfo.get().getType().equals(TokenAuthType.ADMIN) && storeId == null)
                return res.throwError("상점 아이디를 입력해주세요.", "INPUT_CHECK_REQUIRED");
            else if (tokenInfo.get().getType().equals(TokenAuthType.PARTNER) && storeId != tokenInfo.get().getId())
                return res.throwError("본인 가게의 정보만 수정 가능합니다.", "UNAUTHORIZED");
            else storeId = tokenInfo.get().getId();
            Product product = new Product();
            Optional<Store> store = storeService.selectStoreOptional(storeId);
            if (store.isEmpty()) return res.throwError("가게 정보를 찾을 수 없습니다.", "NO_SUCH_DATA");
            product.setStore(store.get());
            Category category = categoryService.findById(categoryId);
            product.setCategory(category);
            if (images.size() == 0) return res.throwError("이미지를 입력해주세요.", "INPUT_CHECK_REQUIRED");
            for (MultipartFile image : images) {
                if (!s3.validateImageType(image)) return res.throwError("허용되지 않는 확장자입니다.", "INPUT_CHECK_REQUIRED");
            }
            if (descriptionImages.size() == 0) return res.throwError("상품 설명 사진을 입력해주세요.", "INPUT_CHECK_REQUIRED");
            for (MultipartFile image : descriptionImages) {
                if (!s3.validateImageType(image)) return res.throwError("허용되지 않는 확장자입니다.", "INPUT_CHECK_REQUIRED");
            }
            title = utils.validateString(title, 100L, "상품");
            product.setTitle(title);
            if (originPrice <= 0) return res.throwError("금액을 확인해주세요.", "INPUT_CHECK_REQUIRED");
            product.setOriginPrice(originPrice);
            if (discountRate != null && discountRate < 0) return res.throwError("할인률을 확인해주세요.", "INPUT_CHECK_REQUIRED");
            product.setDiscountRate(discountRate);
            deliveryInfo = utils.validateString(deliveryInfo, 500L, "배송안내");
            product.setDeliveryInfo(deliveryInfo);

            product.setImages("");
            product.setDescriptionImages("");
            Product result = productService.addProduct(product);
            String
                    imagesUrl =
                    s3.uploadFiles(images, new ArrayList<>(Arrays.asList("product", String.valueOf(result.getId()))));
            String
                    descriptionImagesUrl =
                    s3.uploadFiles(descriptionImages,
                            new ArrayList<>(Arrays.asList("product", String.valueOf(result.getId()))));
            result.setImages(imagesUrl);
            result.setDescriptionImages(descriptionImagesUrl);
            productService.update(result.getId(), result);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<CustomResponse<Product>> updateProduct(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                 @PathVariable(value = "id") Integer id,
                                                                 @RequestPart(value = "categoryId", required = false) Integer categoryId,
                                                                 @RequestPart(value = "images", required = false) List<MultipartFile> images,
                                                                 @RequestPart(value = "title", required = false) String title,
                                                                 @RequestPart(value = "originPrice", required = false) Integer originPrice,
                                                                 @RequestPart(value = "discountRate", required = false) Integer discountRate,
                                                                 @RequestPart(value = "deliveryInfo", required = false) String deliveryInfo,
                                                                 @RequestPart(value = "descriptionImages", required = false) List<MultipartFile> descriptionImages) {
        CustomResponse<Product> res = new CustomResponse<>();
        Optional<TokenInfo>
                tokenInfo =
                jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN, TokenAuthType.PARTNER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Product product = productService.selectProduct(id);
            if (tokenInfo.get().getType().equals(TokenAuthType.PARTNER) &&
                    product.getStoreId() != tokenInfo.get().getId())
                return res.throwError("타지점의 상품입니다.", "UNAUTHORIZED");
            if (categoryId != null) {
                Category category = categoryService.findById(categoryId);
                product.setCategory(category);
            }
            if (images != null) {
                for (MultipartFile image : images) {
                    if (!s3.validateImageType(image)) return res.throwError("허용되지 않는 확장자입니다.", "INPUT_CHECK_REQUIRED");
                }
            }
            if (descriptionImages != null) {
                for (MultipartFile image : descriptionImages) {
                    if (!s3.validateImageType(image)) return res.throwError("허용되지 않는 확장자입니다.", "INPUT_CHECK_REQUIRED");
                }
            }
            if (title != null) {
                title = utils.validateString(title, 100L, "제목");
                product.setTitle(title);
            }
            if (originPrice != null) {
                if (originPrice <= 0) return res.throwError("금액을 확인해주세요.", "INPUT_CHECK_REQUIRED");
                product.setOriginPrice(originPrice);
            }
            if (discountRate != null) {
                if (discountRate < 0) return res.throwError("할인률을 확인해주세요.", "INPUT_CHECK_REQUIRED");
                product.setDiscountRate(discountRate);
            }
            Product result = productService.update(id, product);
            res.setData(Optional.ofNullable(result));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }
}
