package com.matsinger.barofishserver.review.api;

import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.review.application.ReviewCommandService;
import com.matsinger.barofishserver.review.application.ReviewQueryService;
import com.matsinger.barofishserver.review.domain.Review;
import com.matsinger.barofishserver.review.domain.ReviewOrderBy;
import com.matsinger.barofishserver.review.domain.ReviewOrderByType;
import com.matsinger.barofishserver.review.dto.ReviewDto;
import com.matsinger.barofishserver.review.dto.v2.AdminReviewDto;
import com.matsinger.barofishserver.review.dto.UpdateReviewReq;
import com.matsinger.barofishserver.review.dto.v2.ProductReviewDto;
import com.matsinger.barofishserver.review.dto.v2.StoreReviewDto;
import com.matsinger.barofishserver.review.dto.v2.UserReviewDto;
import com.matsinger.barofishserver.utils.CustomResponse;
import com.matsinger.barofishserver.utils.S3.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/review")
public class ReviewControllerV2 {

    private final JwtService jwt;
    private final ReviewQueryService reviewQueryService;
    private final ReviewCommandService reviewCommandService;
    private final S3Uploader s3;

    @GetMapping("/management")
    public ResponseEntity<CustomResponse<Page<AdminReviewDto>>> selectAllReviewListByAdminV2(
            @RequestHeader(value = "Authorization") Optional<String> auth,
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
            @RequestParam(value = "createdAtE", required = false) Timestamp createdAtE
    ) {
        CustomResponse<Page<AdminReviewDto>> res = new CustomResponse<>();
        Optional<TokenInfo>
                tokenInfo =
                jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN, TokenAuthType.PARTNER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");

        PageRequest pageRequest = PageRequest.of(page, take);
        Integer storeId = null;
        if (tokenInfo.get().getType().equals(TokenAuthType.PARTNER)) {
            storeId = tokenInfo.get().getId();
        }

        Page<AdminReviewDto> pagedReviewDtos = reviewQueryService.findAllReviewExceptDeleted(orderBy, sort, orderId, productName, partnerName, reviewer, evaluation, createdAtS, createdAtE, storeId, pageRequest);
        res.setData(Optional.of(pagedReviewDtos));

        return ResponseEntity.ok(res);
    }

    @PostMapping("/{id}")
    public ResponseEntity<CustomResponse<Boolean>> deleteReview(@RequestHeader(value = "Authorization") Optional<String> auth, @PathVariable("id") Integer reviewId) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER, TokenAuthType.PARTNER, TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");

        try {
            Review review = reviewQueryService.selectReview(reviewId);
            if (tokenInfo.isPresent() && tokenInfo.get().getType().equals(TokenAuthType.USER) && review.getUserId() != tokenInfo.get().getId())
                return res.throwError("타인의 리뷰는 삭제할 수 없습니다.", "NOT_ALLOWED");
            else if (tokenInfo.isPresent() && tokenInfo.get().getType().equals(TokenAuthType.PARTNER) && review.getStore().getId() != tokenInfo.get().getId())
                return res.throwError("타 상점의 리뷰입니다.", "NOT_ALLOWED");

            review.setIsDeleted(true);
            res.setData(Optional.ofNullable(true));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<CustomResponse<ProductReviewDto>> getReviews(@PathVariable("id") Integer productId, @RequestHeader(value = "Authorization") Optional<String> auth, @RequestParam(value = "orderType", required = false, defaultValue = "RECENT") ReviewOrderByType orderType, @RequestParam(value = "page", required = false, defaultValue = "0") Integer page, @RequestParam(value = "take", required = false, defaultValue = "10") Integer take) {
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ALLOW), auth);
        PageRequest pageRequest = PageRequest.of(page-1, take);
        CustomResponse<ProductReviewDto> res = new CustomResponse<>();
        try {
            ProductReviewDto pagedProductReviewInfo = reviewQueryService.getPagedProductReviewInfo(productId, tokenInfo.get().getId(), orderType, pageRequest);

            res.setData(Optional.of(pagedProductReviewInfo));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping(value = {"/store/{id}", "/store"})
    public ResponseEntity<CustomResponse<StoreReviewDto>> selectReviewListWithStoreIdV2(@RequestHeader(value = "Authorization") Optional<String> auth, @PathVariable(value = "id", required = false) Integer storeId, @RequestParam(value = "orderType", required = false, defaultValue = "RECENT") ReviewOrderByType orderType, @RequestParam(value = "page", required = false, defaultValue = "1") Integer page, @RequestParam(value = "take", required = false, defaultValue = "10") Integer take) {
        CustomResponse<StoreReviewDto> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ALLOW), auth);

        if (tokenInfo == null && tokenInfo.isEmpty()) {
            return res.throwError("토큰 정보가 유효하지 않습니다.", "01");
        }
        if (tokenInfo.get().getType().equals(TokenAuthType.PARTNER)) storeId = tokenInfo.get().getId();
        Integer userId = null;
        if (tokenInfo.get().getType().equals(TokenAuthType.USER)) userId = tokenInfo.get().getId();

        PageRequest pageRequest = PageRequest.of(page-1, take);

        try {
            StoreReviewDto pagedStoreReviewDto = reviewQueryService.getPagedProductSumStoreReviewInfo(storeId, userId, orderType, pageRequest);
            res.setData(Optional.of(pagedStoreReviewDto));
            return ResponseEntity.ok(res);

        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/my")
    public ResponseEntity<CustomResponse<UserReviewDto>> selectMyReviewListV2(@RequestHeader(value = "Authorization") Optional<String> auth, @RequestParam(value = "orderType", required = false, defaultValue = "RECENT") ReviewOrderByType orderType, @RequestParam(value = "page", required = false, defaultValue = "0") Integer page, @RequestParam(value = "take", required = false, defaultValue = "10") Integer take) {
        CustomResponse<UserReviewDto> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        Integer userId = tokenInfo.get().getId();

        PageRequest pageRequest = PageRequest.of(page-1, take);
        try {
            UserReviewDto pagedUserReview = reviewQueryService.getPagedUserReview(userId, orderType, pageRequest);
            res.setData(Optional.of(pagedUserReview));
            return ResponseEntity.ok(res);

        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<CustomResponse<Object>> updateReviewV2(@RequestHeader(value = "Authorization") Optional<String> auth, @PathVariable("id") Integer id, @RequestPart(value = "data") UpdateReviewReq data, @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        CustomResponse<Object> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Integer userId = tokenInfo.get().getId();

            Integer updatedReviewId = reviewCommandService.update(userId, id, data, images);

            res.setData(Optional.ofNullable(updatedReviewId));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }
}
