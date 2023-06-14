package com.matsinger.barofishserver.review;

import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.order.OrderService;
import com.matsinger.barofishserver.order.object.Orders;
import com.matsinger.barofishserver.product.ProductService;
import com.matsinger.barofishserver.product.object.Product;
import com.matsinger.barofishserver.store.StoreService;
import com.matsinger.barofishserver.store.object.Store;
import com.matsinger.barofishserver.user.UserService;
import com.matsinger.barofishserver.user.object.UserInfo;
import com.matsinger.barofishserver.utils.Common;
import com.matsinger.barofishserver.utils.CustomResponse;
import com.matsinger.barofishserver.utils.S3.S3Uploader;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/review")
public class ReviewController {
    private final ReviewService reviewService;
    private final ProductService productService;
    private final StoreService storeService;
    private final UserService userService;
    private final OrderService orderService;
    private final JwtService jwt;
    private final Common utils;
    private final S3Uploader s3;


    @GetMapping("/management")
    public ResponseEntity<CustomResponse<Page<ReviewDto>>> selectAllReviewListByAdmin(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                                      @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                                                                      @RequestParam(value = "take", required = false, defaultValue = "10") Integer take) {
        CustomResponse<Page<ReviewDto>> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Page<ReviewDto> reviews = reviewService.selectAllReviewList(page - 1, take).map(review -> {
                ReviewDto dto = reviewService.convert2Dto(review);
                dto.setSimpleProduct(productService.selectProduct(review.getProduct().getId()).convert2ListDto());
                return reviewService.convert2Dto(review);
            });

            res.setData(Optional.ofNullable(reviews));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping(value = {"/store/{id}", "/store"})
    public ResponseEntity<CustomResponse<Page<ReviewDto>>> selectReviewListWithStoreId(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                                       @PathVariable(value = "id", required = false) Integer storeId,
                                                                                       @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                                                                       @RequestParam(value = "take", required = false, defaultValue = "10") Integer take) {
        CustomResponse<Page<ReviewDto>> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ALLOW), auth);
        try {
            if (tokenInfo != null && tokenInfo.isPresent() && tokenInfo.get().getType().equals(TokenAuthType.PARTNER))
                storeId = tokenInfo.get().getId();
            Page<ReviewDto> reviews = reviewService.selectReviewListByStore(storeId, page, take).map(review -> {
                ReviewDto dto = reviewService.convert2Dto(review);
                dto.setSimpleProduct(productService.selectProduct(review.getProduct().getId()).convert2ListDto());
                return reviewService.convert2Dto(review);
            });
            res.setData(Optional.ofNullable(reviews));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomResponse<ReviewDto>> selectReview(@PathVariable("id") Integer id) {
        CustomResponse<ReviewDto> res = new CustomResponse<>();
        try {
            Review review = reviewService.selectReview(id);
            ReviewDto reviewDto = reviewService.convert2Dto(review);
            reviewDto.setSimpleProduct(review.getProduct().convert2ListDto());
            res.setData(Optional.of(reviewDto));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<CustomResponse<Page<ReviewDto>>> selectReviewListWithProductId(@PathVariable("id") Integer productId,
                                                                                         @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                                                                         @RequestParam(value = "take", required = false, defaultValue = "10") Integer take) {
        CustomResponse<Page<ReviewDto>> res = new CustomResponse<>();
        try {
            Page<ReviewDto> reviews = reviewService.selectReviewListByProduct(productId, page, take).map(review -> {
                ReviewDto dto = reviewService.convert2Dto(review);
                dto.setSimpleProduct(productService.selectProduct(review.getProduct().getId()).convert2ListDto());
                return reviewService.convert2Dto(review);
            });
            res.setData(Optional.ofNullable(reviews));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @Getter
    @NoArgsConstructor
    class ReviewAddReq {
        Integer productId;
        Integer storeId;
        Integer userId;
        String orderId;
        ReviewEvaluation evaluation;
        String content;
    }

    @PostMapping("/add")
    public ResponseEntity<CustomResponse<ReviewDto>> addReviewByUser(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                     @RequestPart(value = "data") ReviewAddReq data,
                                                                     @RequestPart(value = "images") List<MultipartFile> images) {
        CustomResponse<ReviewDto> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Integer userId = tokenInfo.get().getId();
            UserInfo user = userService.selectUserInfo(userId);
            Orders order = orderService.selectOrder(data.getOrderId());
            Product product = productService.selectProduct(data.getProductId());
            Store store = storeService.selectStore(data.getStoreId());
            String content = data.getContent();
            if (content.length() == 0) return res.throwError("내용을 입력해주세요.", "INPUT_CHECK_REQUIRED");
            Review
                    review =
                    Review.builder().product(product).store(store).userId(userId).evaluation(data.getEvaluation()).content(
                            content).createdAt(utils.now()).images("").orderId(order.getId()).build();
            Review result = reviewService.addReview(review);
            String
                    imgUrls =
                    s3.uploadFiles(images,
                            new ArrayList<>(Arrays.asList("review", String.valueOf(result.getId())))).toString();
            result.setImages(imgUrls);
            result = reviewService.updateReview(review);
            res.setData(Optional.ofNullable(result.convert2Dto()));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse<Boolean>> deleteReviewByUser(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                      @PathVariable("id") Integer reviewId) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo>
                tokenInfo =
                jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER, TokenAuthType.PARTNER, TokenAuthType.ADMIN),
                        auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Review review = reviewService.selectReview(reviewId);
            if (tokenInfo.isPresent() &&
                    tokenInfo.get().getType().equals(TokenAuthType.USER) &&
                    review.getUserId() != tokenInfo.get().getId())
                return res.throwError("타인의 레뷰는 삭제할 수 없습니다.", "NOT_ALLOWED");
            else if (tokenInfo.isPresent() &&
                    tokenInfo.get().getType().equals(TokenAuthType.PARTNER) &&
                    review.getStore().getId() != tokenInfo.get().getId())
                return res.throwError("타 상점의 리뷰입니다.", "NOT_ALLOWED");
            Boolean result = reviewService.deleteReview(reviewId);
            res.setData(Optional.ofNullable(result));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }
}
