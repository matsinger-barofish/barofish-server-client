package com.matsinger.barofishserver.review.api;

import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.review.application.ReviewQueryService;
import com.matsinger.barofishserver.review.domain.ReviewOrderByType;
import com.matsinger.barofishserver.review.dto.v2.ProductReviewDto;
import com.matsinger.barofishserver.utils.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/review")
public class ReviewControllerV2 {

    private final JwtService jwt;
    private final ReviewQueryService reviewQueryService;

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
}
