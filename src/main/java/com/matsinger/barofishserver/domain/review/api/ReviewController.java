package com.matsinger.barofishserver.domain.review.api;

import com.matsinger.barofishserver.domain.review.application.ReviewCommandService;
import com.matsinger.barofishserver.domain.review.application.ReviewQueryService;
import com.matsinger.barofishserver.domain.review.domain.*;
import com.matsinger.barofishserver.global.error.ErrorCode;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.domain.order.application.OrderService;
import com.matsinger.barofishserver.domain.product.application.ProductService;
import com.matsinger.barofishserver.domain.product.domain.Product;
import com.matsinger.barofishserver.domain.review.dto.ReviewAddReq;
import com.matsinger.barofishserver.domain.review.dto.ReviewDto;
import com.matsinger.barofishserver.domain.review.dto.UpdateReviewReq;
import com.matsinger.barofishserver.domain.store.application.StoreService;
import com.matsinger.barofishserver.domain.store.domain.Store;
import com.matsinger.barofishserver.domain.user.application.UserCommandService;
import com.matsinger.barofishserver.domain.userinfo.domain.UserInfo;
import com.matsinger.barofishserver.domain.order.orderprductinfo.domain.OrderProductInfo;
import com.matsinger.barofishserver.jwt.exception.JwtBusinessException;
import com.matsinger.barofishserver.utils.Common;
import com.matsinger.barofishserver.utils.CustomResponse;
import com.matsinger.barofishserver.utils.S3.S3Uploader;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
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
    private final ReviewCommandService reviewCommandService;
    private final ReviewQueryService reviewQueryService;
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

                TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN, TokenAuthType.PARTNER), auth);

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
            if (tokenInfo.getType().equals(TokenAuthType.PARTNER))
                predicates.add(builder.equal(root.get("product").get("storeId"), tokenInfo.getId()));
            if (evaluation != null) {
                Join<Review, ReviewEvaluation> t = root.join("evaluations", JoinType.LEFT);
                predicates.add(builder.and(t.get("evaluation").in(Arrays.stream(evaluation.split(",")).map(
                        ReviewEvaluationType::valueOf).toList())));
            }
            return builder.and(predicates.toArray(new Predicate[0]));
        };
        PageRequest pageRequest = PageRequest.of(page, take, Sort.by(sort, orderBy.label));
        Page<ReviewDto> reviews = reviewQueryService.selectAllReviewListExceptDeleted(spec, pageRequest).map(review -> {
            ReviewDto dto = reviewCommandService.convert2Dto(review);
            dto.setSimpleProduct(productService.convert2ListDto(productService.findById(review.getProduct().getId())));
            return dto;
        });
        res.setData(Optional.of(reviews));
        return ResponseEntity.ok(res);
    }

    @GetMapping(value = {"/store/{id}", "/store"})
    public ResponseEntity<CustomResponse<Page<ReviewDto>>> selectReviewListWithStoreId(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                                       @PathVariable(value = "id", required = false) Integer storeId,
                                                                                       @RequestParam(value = "orderType", required = false, defaultValue = "RECENT") ReviewOrderByType orderType,
                                                                                       @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                                                                       @RequestParam(value = "take", required = false, defaultValue = "10") Integer take) {
        CustomResponse<Page<ReviewDto>> res = new CustomResponse<>();

        Integer userId = null;
        TokenInfo tokenInfo = null;
        if (auth.isEmpty()) {
            userId = null;
        }
        if (auth.isPresent()) {
            tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ALLOW, TokenAuthType.USER), auth);
            userId = tokenInfo.getId();
        }

        PageRequest pageRequest = PageRequest.of(page, take);
        Page<Review> reviewData = null;
        if (orderType.equals(ReviewOrderByType.RECENT))
            reviewData = reviewQueryService.selectReviewListByStoreOrderedRecent(storeId, pageRequest);
        else reviewData = reviewQueryService.selectReviewListOrderedBestWithStoreId(storeId, pageRequest);

        Integer finalUserId = userId;
        Page<ReviewDto> reviews = reviewData.map(review -> {
            ReviewDto dto = reviewCommandService.convert2Dto(review, finalUserId);
            dto.setSimpleProduct(productService.selectProduct(review.getProduct().getId()).convert2ListDto());
            return dto;
        });
        res.setData(Optional.of(reviews));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomResponse<ReviewDto>> selectReview(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                  @PathVariable("id") Integer id) {
        CustomResponse<ReviewDto> res = new CustomResponse<>();

        Integer userId = null;
        TokenInfo tokenInfo = null;
        if (auth.isEmpty()) {
            userId = null;
        }
        if (auth.isPresent()) {
            tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ALLOW, TokenAuthType.USER), auth);
            userId = tokenInfo.getId();
        }

        Review review = reviewQueryService.selectReview(id);
        ReviewDto reviewDto = reviewCommandService.convert2Dto(review, userId);
        reviewDto.setSimpleProduct(review.getProduct().convert2ListDto());
        res.setData(Optional.of(reviewDto));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<CustomResponse<Page<ReviewDto>>> selectReviewListWithProductId(@PathVariable("id") Integer productId,
                                                                                         @RequestHeader(value = "Authorization") Optional<String> auth,
                                                                                         @RequestParam(value = "orderType", required = false, defaultValue = "RECENT") ReviewOrderByType orderType,
                                                                                         @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                                                                         @RequestParam(value = "take", required = false, defaultValue = "10") Integer take) {
        CustomResponse<Page<ReviewDto>> res = new CustomResponse<>();

        Integer userId = null;
        TokenInfo tokenInfo = null;
        if (auth.isEmpty()) {
            userId = null;
        }
        if (auth.isPresent()) {
            tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ALLOW, TokenAuthType.USER), auth);
            userId = tokenInfo.getId();
        }

        PageRequest pageRequest = PageRequest.of(page, take);
        Page<Review>
                reviewData =
                orderType.equals(ReviewOrderByType.RECENT) ? reviewQueryService.selectReviewListByProduct(productId,
                        pageRequest) : reviewQueryService.selectReviewListOrderedBestWithProductId(productId,
                        pageRequest);

        Integer finalUserId = userId;
        Page<ReviewDto> reviews = reviewData.map(review -> {
            ReviewDto dto = new ReviewDto();
            if (finalUserId != null) {
                dto = reviewCommandService.convert2Dto(review, finalUserId);
            } else dto = reviewCommandService.convert2Dto(review);
            dto.setSimpleProduct(productService.selectProduct(review.getProduct().getId()).convert2ListDto());
            return dto;
        });
        res.setData(Optional.of(reviews));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/my")
    public ResponseEntity<CustomResponse<Page<ReviewDto>>> selectMyReviewList(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                              @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                                                              @RequestParam(value = "take", required = false, defaultValue = "10") Integer take) {
        CustomResponse<Page<ReviewDto>> res = new CustomResponse<>();

                TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        Integer userId = tokenInfo.getId();

        Page<ReviewDto>
                reviews =
                reviewQueryService.selectAllReviewListByUserId(userId, page - 1, take).map(review -> {
                    ReviewDto dto = reviewCommandService.convert2Dto(review, userId);
                    dto.setSimpleProduct(productService.convert2ListDto(productService.selectProduct(review.getProductId())));
                    return dto;
                });
        res.setData(Optional.of(reviews));
        return ResponseEntity.ok(res);
    }


    @PostMapping("/add")
    public ResponseEntity<CustomResponse<ReviewDto>> addReviewByUser(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                     @RequestPart(value = "data") ReviewAddReq data,
                                                                     @RequestPart(value = "images", required = false) List<MultipartFile> images) throws Exception {
        CustomResponse<ReviewDto> res = new CustomResponse<>();

                TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);

        Integer userId = tokenInfo.getId();
        UserInfo user = userService.selectUserInfo(userId);
        if (data.getOrderProductInfoId() == null)
            throw new IllegalArgumentException("주문 상품 아이디를 입력해주세요.");
        OrderProductInfo orderProductInfo = orderService.selectOrderProductInfo(data.getOrderProductInfoId());
        if (data.getProductId() == null) throw new IllegalArgumentException("상품 아이디를 입력해주세요.");
        Product product = productService.findById(data.getProductId());
        Store store = storeService.selectStore(product.getStoreId());
        String content = data.getContent();
        Boolean
                isWritten =
                reviewQueryService.checkReviewWritten(userId, product.getId(), orderProductInfo.getId());
        if (isWritten) throw new IllegalArgumentException("이미 리뷰를 작성하였습니다.");
        if (content.length() == 0) throw new IllegalArgumentException("내용을 입력해주세요.");
        Review
                review =
                Review.builder().productId(product.getId()).store(store).storeId(store.getId()).userId(userId).content(
                        content).createdAt(utils.now()).images("").orderProductInfoId(orderProductInfo.getId()).build();
        Review result = reviewCommandService.addReview(review);
        reviewCommandService.addReviewEvaluationList(result.getId(), data.getEvaluations());
        if (images != null) {
            String
                    imgUrls =
                    s3.uploadFiles(images,
                            new ArrayList<>(Arrays.asList("review", String.valueOf(result.getId())))).toString();
            result.setImages(imgUrls);
        } else result.setImages("[]");
        result = reviewCommandService.updateReview(review);
        reviewCommandService.increaseUserPoint(userId, images != null);
        res.setData(Optional.ofNullable(result.convert2Dto()));
        return ResponseEntity.ok(res);
    }

    @PostMapping(value = "/update/{id}")
    public ResponseEntity<CustomResponse<ReviewDto>> updateReview(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                  @PathVariable("id") Integer id,
                                                                  @RequestPart(value = "data") UpdateReviewReq data,
                                                                  @RequestPart(value = "imageUrlsToRemain", required = false) List<String> imageUrlsToRemain,
                                                                  @RequestPart(value = "newImages", required = false) List<MultipartFile> newImages) throws Exception {
        CustomResponse<ReviewDto> res = new CustomResponse<>();

                TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        Integer userId = tokenInfo.getId();

        Review review = reviewQueryService.selectReview(id);
        if (review.getUserId() != userId) throw new IllegalArgumentException("타인의 리뷰입니다.");
        if (data.getContent() != null) {
            if (data.getContent().length() == 0) throw new IllegalArgumentException("리뷰 내용을 입력해주세요.");
            review.setContent(data.getContent());
        }
        if (data.getEvaluations() != null) {
            reviewCommandService.deleteReviewWithReviewId(review.getId());
            reviewCommandService.addReviewEvaluationList(review.getId(), data.getEvaluations());
        }

        if (imageUrlsToRemain == null && newImages != null) {
            if (!review.getImageUrls().isEmpty()) {
                for (String imageUrl : review.getImageUrls()) {
                    s3.deleteFile(imageUrl);
                }
                review.setImages("[]");
            }
            List<String> uploadedImageUrls = s3.uploadFiles(newImages, new ArrayList<>(Arrays.asList("review", String.valueOf(id))));
            review.setImages(uploadedImageUrls.toString());
        }

        if (imageUrlsToRemain == null && newImages == null) {
            if (!review.getImageUrls().isEmpty()) {
                for (String imageUrl : review.getImageUrls()) {
                    s3.deleteFile(imageUrl);
                }
                review.setImages("[]");
            }
        }

        if (imageUrlsToRemain != null && newImages == null) {
            List<String> newImageUrls = deleteImagesAndReturnRemainingImageUrls(id, review, imageUrlsToRemain);
            review.setImages(newImageUrls.toString());
        }

        if (imageUrlsToRemain != null && newImages != null) {
            List<String> newImageUrls = deleteImagesAndReturnRemainingImageUrls(id, review, imageUrlsToRemain);

            List<String> uploadedImageUrls = s3.uploadFiles(newImages, new ArrayList<>(Arrays.asList("review", String.valueOf(id))));
            newImageUrls.addAll(uploadedImageUrls);

            review.setImages(newImageUrls.toString());
        }

        review = reviewCommandService.updateReview(review);
        res.setData(Optional.ofNullable(reviewCommandService.convert2Dto(review)));
        return ResponseEntity.ok(res);
    }

    private List<String> deleteImagesAndReturnRemainingImageUrls (Integer id, Review review, List<String> imageUrlsToRemain) {
        ArrayList<String> ImageUrlsToRemain = new ArrayList<>();
        ArrayList<String> reviewImageUrls = review.getImageUrls();

        for (String imageUrlToRemain : imageUrlsToRemain) {
            boolean isRemoved = reviewImageUrls.remove(imageUrlToRemain);
            if (isRemoved) {
                ImageUrlsToRemain.add(imageUrlToRemain);
            }
        }
        for (String imageUrlToDelete : reviewImageUrls) {
            s3.deleteFile("review/" + id + "/" + imageUrlToDelete);
        }
        return ImageUrlsToRemain;
    }

    @PostMapping("/like/{id}")
    public ResponseEntity<CustomResponse<Boolean>> likeReviewByUser(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                    @PathVariable("id") Integer reviewId) {
        CustomResponse<Boolean> res = new CustomResponse<>();

                TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        Integer userId = tokenInfo.getId();

        Review review = reviewQueryService.selectReview(reviewId);
        reviewCommandService.likeReview(userId, reviewId);
        res.setData(Optional.of(true));
        return ResponseEntity.ok(res);
    }

    @PostMapping("/unlike/{id}")
    public ResponseEntity<CustomResponse<Boolean>> unlikeReviewByUser(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                      @PathVariable("id") Integer reviewId) {
        CustomResponse<Boolean> res = new CustomResponse<>();

                TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        Integer userId = tokenInfo.getId();
        Review review = reviewQueryService.selectReview(reviewId);
        reviewCommandService.unlikeReview(userId, review.getId());
        res.setData(Optional.of(false));
        return ResponseEntity.ok(res);
    }


    @PostMapping("/delete/{id}")
    public ResponseEntity<CustomResponse<Boolean>> deleteReviewByUser(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                      @PathVariable("id") Integer reviewId) {
        CustomResponse<Boolean> res = new CustomResponse<>();

                TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER, TokenAuthType.PARTNER, TokenAuthType.ADMIN), auth);

        Review review = reviewQueryService.selectReview(reviewId);
        if (tokenInfo.getType().equals(TokenAuthType.USER) &&
                review.getUserId() != tokenInfo.getId())
            throw new IllegalArgumentException("타인의 리뷰는 삭제할 수 없습니다.");
        else if (tokenInfo.getType().equals(TokenAuthType.PARTNER) &&
                review.getStore().getId() != tokenInfo.getId())
            throw new IllegalArgumentException("타 상점의 리뷰입니다.");
        Boolean result = reviewCommandService.deleteReview(reviewId);
        res.setData(Optional.ofNullable(result));
        return ResponseEntity.ok(res);
    }
}
