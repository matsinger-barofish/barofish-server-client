package com.matsinger.barofishserver.review.api;

import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.order.application.OrderService;
import com.matsinger.barofishserver.product.application.ProductService;
import com.matsinger.barofishserver.product.domain.Product;
import com.matsinger.barofishserver.review.application.ReviewService;
import com.matsinger.barofishserver.review.domain.*;
import com.matsinger.barofishserver.review.dto.ReviewDto;
import com.matsinger.barofishserver.store.application.StoreService;
import com.matsinger.barofishserver.store.domain.Store;
import com.matsinger.barofishserver.user.application.UserCommandService;
import com.matsinger.barofishserver.userinfo.domain.UserInfo;
import com.matsinger.barofishserver.order.orderprductinfo.domain.OrderProductInfo;
import com.matsinger.barofishserver.utils.Common;
import com.matsinger.barofishserver.utils.CustomResponse;
import com.matsinger.barofishserver.utils.S3.S3Uploader;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/review")
public class ReviewController {
    private final ReviewService reviewService;
    private final ProductService productService;
    private final StoreService storeService;
    private final UserCommandService userService;
    private final OrderService orderService;
    private final JwtService jwt;
    private final Common utils;
    private final S3Uploader s3;


    @GetMapping("/management")
    public ResponseEntity<CustomResponse<Page<ReviewDto>>> selectAllReviewListByAdmin(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                                      @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                                                                      @RequestParam(value = "take", required = false, defaultValue = "10") Integer take,
                                                                                      @RequestParam(value = "orderby", defaultValue = "createdAt") ReviewOrderBy orderBy,
                                                                                      @RequestParam(value = "orderType", defaultValue = "DESC") Sort.Direction sort,
                                                                                      @RequestParam(value = "orderNo", required = false) String orderId,
                                                                                      @RequestParam(value = "productName", required = false) String productName,
                                                                                      @RequestParam(value = "partnerName", required = false) String partnerName,
                                                                                      @RequestParam(value = "reviewer", required = false) String reviewer,
                                                                                      @RequestParam(value = "evaluation", required = false) String evaluation,
                                                                                      @RequestParam(value = "createdAtS", required = false) Timestamp createdAtS,
                                                                                      @RequestParam(value = "createdAtE", required = false) Timestamp createdAtE) {
        CustomResponse<Page<ReviewDto>> res = new CustomResponse<>();
        Optional<TokenInfo>
                tokenInfo =
                jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN, TokenAuthType.PARTNER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Specification<Review> spec = (root, query, builder) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (orderId != null) predicates.add(builder.like(root.get("order").get("id"), "%" + orderId + "%"));
                if (productName != null)
                    predicates.add(builder.like(root.get("product").get("title"), "%" + productName + "%"));
                if (partnerName != null) predicates.add(builder.like(root.get("store").get("storeInfo").get("name"),
                        "%" + partnerName + "%"));
                if (reviewer != null)
                    predicates.add(builder.like(root.get("user").get("userInfo").get("name"), "%" + reviewer + "%"));
                if (createdAtS != null) predicates.add(builder.greaterThan(root.get("createdAt"), createdAtS));
                if (createdAtE != null) predicates.add(builder.lessThan(root.get("createdAt"), createdAtE));
                if (tokenInfo.get().getType().equals(TokenAuthType.PARTNER))
                    predicates.add(builder.equal(root.get("product").get("storeId"), tokenInfo.get().getId()));
                if (evaluation != null) {
                    Join<Review, ReviewEvaluation> t = root.join("evaluations", JoinType.LEFT);
                    predicates.add(builder.and(t.get("evaluation").in(Arrays.stream(evaluation.split(",")).map(
                            ReviewEvaluationType::valueOf).toList())));
                }
                return builder.and(predicates.toArray(new Predicate[0]));
            };
            PageRequest pageRequest = PageRequest.of(page, take, Sort.by(sort, orderBy.label));
            Page<ReviewDto> reviews = reviewService.selectAllReviewList(spec, pageRequest).map(review -> {
                ReviewDto dto = reviewService.convert2Dto(review);
                dto.setSimpleProduct(productService.convert2ListDto(productService.findById(review.getProduct().getId())));
                return dto;
            });

            res.setData(Optional.of(reviews));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping(value = {"/store/{id}", "/store"})
    public ResponseEntity<CustomResponse<Page<ReviewDto>>> selectReviewListWithStoreId(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                                       @PathVariable(value = "id", required = false) Integer storeId,
                                                                                       @RequestParam(value = "orderType", required = false, defaultValue = "RECENT") ReviewOrderByType orderType,
                                                                                       @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                                                                       @RequestParam(value = "take", required = false, defaultValue = "10") Integer take) {
        CustomResponse<Page<ReviewDto>> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ALLOW), auth);
        try {
            if (tokenInfo != null && tokenInfo.isPresent() && tokenInfo.get().getType().equals(TokenAuthType.PARTNER))
                storeId = tokenInfo.get().getId();
            Integer
                    userId =
                    tokenInfo != null &&
                            tokenInfo.isPresent() &&
                            tokenInfo.get().getType().equals(TokenAuthType.USER) ? tokenInfo.get().getId() : null;
            PageRequest pageRequest = PageRequest.of(page, take);
            Page<Review>
                    reviewData =
                    orderType.equals(ReviewOrderByType.RECENT) ? reviewService.selectReviewListByStoreOrderedRecent(
                            storeId,
                            pageRequest) : reviewService.selectReviewListOrderedBestWithStoreId(storeId, pageRequest);

            Page<ReviewDto> reviews = reviewData.map(review -> {
                ReviewDto dto = reviewService.convert2Dto(review, userId);
                dto.setSimpleProduct(productService.selectProduct(review.getProduct().getId()).convert2ListDto());
                return dto;
            });
            res.setData(Optional.of(reviews));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomResponse<ReviewDto>> selectReview(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                  @PathVariable("id") Integer id) {
        CustomResponse<ReviewDto> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ALLOW), auth);
        try {
            Integer
                    userId =
                    tokenInfo != null &&
                            tokenInfo.isPresent() &&
                            tokenInfo.get().getType().equals(TokenAuthType.USER) ? tokenInfo.get().getId() : null;
            Review review = reviewService.selectReview(id);
            ReviewDto reviewDto = reviewService.convert2Dto(review, userId);
            reviewDto.setSimpleProduct(review.getProduct().convert2ListDto());
            res.setData(Optional.of(reviewDto));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<CustomResponse<Page<ReviewDto>>> selectReviewListWithProductId(@PathVariable("id") Integer productId,
                                                                                         @RequestHeader(value = "Authorization") Optional<String> auth,
                                                                                         @RequestParam(value = "orderType", required = false, defaultValue = "RECENT") ReviewOrderByType orderType,
                                                                                         @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                                                                         @RequestParam(value = "take", required = false, defaultValue = "10") Integer take) {
        CustomResponse<Page<ReviewDto>> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ALLOW), auth);
        try {
            PageRequest pageRequest = PageRequest.of(page, take);
            Page<Review>
                    reviewData =
                    orderType.equals(ReviewOrderByType.RECENT) ? reviewService.selectReviewListByProduct(productId,
                            pageRequest) : reviewService.selectReviewListOrderedBestWithProductId(productId,
                            pageRequest);
            Page<ReviewDto> reviews = reviewData.map(review -> {
                ReviewDto
                        dto =
                        tokenInfo != null &&
                                tokenInfo.isPresent() &&
                                tokenInfo.get().getType().equals(TokenAuthType.USER) ? reviewService.convert2Dto(review,
                                tokenInfo.get().getId()) : reviewService.convert2Dto(review);
                dto.setSimpleProduct(productService.selectProduct(review.getProduct().getId()).convert2ListDto());
                return dto;
            });
            res.setData(Optional.of(reviews));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/my")
    public ResponseEntity<CustomResponse<Page<ReviewDto>>> selectMyReviewList(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                              @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                                                              @RequestParam(value = "take", required = false, defaultValue = "10") Integer take) {
        CustomResponse<Page<ReviewDto>> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Integer userId = tokenInfo.get().getId();
            Page<ReviewDto> reviews = reviewService.selectAllReviewListByUserId(userId, page - 1, take).map(review -> {
                ReviewDto dto = reviewService.convert2Dto(review, userId);
                dto.setSimpleProduct(productService.convert2ListDto(productService.selectProduct(review.getProductId())));
                return dto;
            });
            res.setData(Optional.of(reviews));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @Getter
    @NoArgsConstructor
    private static class ReviewAddReq {
        Integer productId;
        Integer userId;
        Integer orderProductInfoId;
        List<ReviewEvaluationType> evaluations;
        String content;
    }

    @PostMapping("/add")
    public ResponseEntity<CustomResponse<ReviewDto>> addReviewByUser(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                     @RequestPart(value = "data") ReviewAddReq data,
                                                                     @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        CustomResponse<ReviewDto> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Integer userId = tokenInfo.get().getId();
            UserInfo user = userService.selectUserInfo(userId);
            if (data.orderProductInfoId == null) return res.throwError("주문 상품 아이디를 입력해주세요.", "INPUT_CHECK_REQUIRED");
            OrderProductInfo orderProductInfo = orderService.selectOrderProductInfo(data.orderProductInfoId);
            if (data.productId == null) return res.throwError("상품 아이디를 입력해주세요.", "INPUT_CHECK_REQUIRED");
            Product product = productService.findById(data.getProductId());
            Store store = storeService.selectStore(product.getStoreId());
            String content = data.getContent();
            Boolean isWritten = reviewService.checkReviewWritten(userId, product.getId(), orderProductInfo.getId());
            if (isWritten) return res.throwError("이미 리뷰를 작성하였습니다.", "NOT_ALLOWED");
            if (content.length() == 0) return res.throwError("내용을 입력해주세요.", "INPUT_CHECK_REQUIRED");
            Review
                    review =
                    Review.builder().productId(product.getId()).store(store).storeId(store.getId()).userId(userId).content(
                            content).createdAt(utils.now()).images("").orderProductInfoId(orderProductInfo.getId()).build();
            Review result = reviewService.addReview(review);
            reviewService.addReviewEvaluationList(result.getId(), data.evaluations);
            if (images != null) {
                String
                        imgUrls =
                        s3.uploadFiles(images,
                                new ArrayList<>(Arrays.asList("review", String.valueOf(result.getId())))).toString();
                result.setImages(imgUrls);
            } else result.setImages("[]");
            result = reviewService.updateReview(review);
            reviewService.increaseUserPoint(userId, images != null);
            res.setData(Optional.ofNullable(result.convert2Dto()));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }

    }

    @Getter
    @NoArgsConstructor
    private static class UpdateReviewReq {
        String content;
        List<ReviewEvaluationType> evaluations;
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<CustomResponse<ReviewDto>> updateReview(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                  @PathVariable("id") Integer id,
                                                                  @RequestPart(value = "data") UpdateReviewReq data,
                                                                  @RequestPart(value = "existImages") List<String> existingImages,
                                                                  @RequestPart(value = "newImages") List<MultipartFile> newImages) {
        CustomResponse<ReviewDto> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Integer userId = tokenInfo.get().getId();
            Review review = reviewService.selectReview(id);
            if (review.getUserId() != userId) return res.throwError("타인의 리뷰입니다.", "NOT_ALLOWED");
            if (data.content != null) {
                if (data.content.length() == 0) return res.throwError("리뷰 내용을 입력해주세요.", "INPUT_CHECK_REQUIRED");
                review.setContent(data.content);
            }
            if (data.evaluations != null) {
                reviewService.deleteReviewWithReviewId(review.getId());
                reviewService.addReviewEvaluationList(review.getId(), data.evaluations);
            }
            if (existingImages != null || newImages != null) {
                List<String> imgUrls = existingImages;
                if (newImages != null) existingImages.addAll(s3.uploadFiles(newImages,
                        new ArrayList<>(Arrays.asList("review", String.valueOf(id)))));
                review.setImages(imgUrls.toString());
            }
            review = reviewService.updateReview(review);
            res.setData(Optional.ofNullable(reviewService.convert2Dto(review)));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @PostMapping("/like/{id}")
    public ResponseEntity<CustomResponse<Boolean>> likeReviewByUser(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                    @PathVariable("id") Integer reviewId) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Integer userId = tokenInfo.get().getId();
            Review review = reviewService.selectReview(reviewId);
            reviewService.likeReview(userId, reviewId);
            res.setData(Optional.of(true));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @PostMapping("/unlike/{id}")
    public ResponseEntity<CustomResponse<Boolean>> unlikeReviewByUser(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                      @PathVariable("id") Integer reviewId) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Integer userId = tokenInfo.get().getId();
            Review review = reviewService.selectReview(reviewId);
            reviewService.unlikeReview(userId, reviewId);
            res.setData(Optional.of(false));
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
