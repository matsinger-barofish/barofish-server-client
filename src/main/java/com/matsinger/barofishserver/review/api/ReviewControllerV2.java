package com.matsinger.barofishserver.review.api;

import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.review.application.ReviewCommandService;
import com.matsinger.barofishserver.review.application.ReviewQueryService;
import com.matsinger.barofishserver.review.domain.Review;
import com.matsinger.barofishserver.review.domain.ReviewOrderByType;
import com.matsinger.barofishserver.review.dto.ReviewDto;
import com.matsinger.barofishserver.review.dto.UpdateReviewReq;
import com.matsinger.barofishserver.review.dto.v2.ProductReviewDto;
import com.matsinger.barofishserver.utils.CustomResponse;
import com.matsinger.barofishserver.utils.S3.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/review")
public class ReviewControllerV2 {

    private final JwtService jwt;
    private final ReviewQueryService reviewQueryService;
    private final ReviewCommandService reviewCommandService;
    private final S3Uploader s3;

    @GetMapping("/product/{id}")
    public ResponseEntity<CustomResponse<ProductReviewDto>> getReviews(@PathVariable("id") Integer productId,
                                                                       @RequestHeader(value = "Authorization") Optional<String> auth,
                                                                       @RequestParam(value = "orderType", required = false, defaultValue = "RECENT") ReviewOrderByType orderType,
                                                                       @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                                                       @RequestParam(value = "take", required = false, defaultValue = "10") Integer take) {
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ALLOW), auth);
        PageRequest pageRequest = PageRequest.of(page, take);
        CustomResponse<ProductReviewDto> res = new CustomResponse<>();
        try {
            if (tokenInfo.get().getType().equals(TokenAuthType.USER)) {
                ProductReviewDto pagedProductReviewInfo = reviewQueryService.getPagedProductReviewInfo(productId, orderType, pageRequest);

                res.setData(Optional.of(pagedProductReviewInfo));
                return ResponseEntity.ok(res);
            }

            res.throwError("토큰 정보가 유효하지 않습니다.", "01");
            return ResponseEntity.ok(res);

        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @PostMapping("/{id}")
    public ResponseEntity<CustomResponse<Boolean>> deleteReview(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                @PathVariable("id") Integer reviewId) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo>
                tokenInfo =
                jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER, TokenAuthType.PARTNER, TokenAuthType.ADMIN),
                        auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");

        try {
            Review review = reviewQueryService.selectReview(reviewId);
            if (tokenInfo.isPresent() &&
                    tokenInfo.get().getType().equals(TokenAuthType.USER) &&
                    review.getUserId() != tokenInfo.get().getId())
                return res.throwError("타인의 리뷰는 삭제할 수 없습니다.", "NOT_ALLOWED");
            else if (tokenInfo.isPresent() &&
                    tokenInfo.get().getType().equals(TokenAuthType.PARTNER) &&
                    review.getStore().getId() != tokenInfo.get().getId())
                return res.throwError("타 상점의 리뷰입니다.", "NOT_ALLOWED");

            review.setIsDeleted(true);
            res.setData(Optional.ofNullable(true));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<CustomResponse<Object>> updateReview(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                  @PathVariable("id") Integer id,
                                                                  @RequestPart(value = "data") UpdateReviewReq data,
                                                                  @RequestPart(value = "images", required = false) List<MultipartFile> images) {
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
