package com.matsinger.barofishserver.review.api;

import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
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

    @GetMapping("/product/{id}")
    public ResponseEntity<CustomResponse<Page<ProductReviewDto>>> getReviews(@PathVariable("id") Integer productId,
                                                                             @RequestHeader(value = "Authorization") Optional<String> auth,
                                                                             @RequestParam(value = "orderType", required = false, defaultValue = "RECENT") ReviewOrderByType orderType,
                                                                             @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                                                             @RequestParam(value = "take", required = false, defaultValue = "10") Integer take) {
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ALLOW), auth);
        PageRequest pageRequest = PageRequest.of(page, take);
        try {
            if (tokenInfo.get().getType().equals(TokenAuthType.USER)) {

            }
            return new ResponseEntity<>(null);
        } catch (Exception e) {
            return new ResponseEntity<>(null);
        }
    }
}
