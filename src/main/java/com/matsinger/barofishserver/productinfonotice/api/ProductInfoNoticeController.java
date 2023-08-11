package com.matsinger.barofishserver.productinfonotice.api;

import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.product.api.ProductController;
import com.matsinger.barofishserver.productinfonotice.application.ProductInfoNotificationQueryService;
import com.matsinger.barofishserver.utils.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product-info-notice")
public class ProductInfoNoticeController {

    private final JwtService jwt;
    private ProductInfoNotificationQueryService productInfoNotificationQueryService;

    @GetMapping("/")
    public ResponseEntity<CustomResponse<Object>> getProduct(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                       @RequestParam(value = "id", required = false) Integer itemCode) {
        CustomResponse<Object> res = new CustomResponse<>();
        Set<TokenAuthType> permission = Set.of(TokenAuthType.ADMIN, TokenAuthType.PARTNER);
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(permission, auth);
        if (tokenInfo == null) {
            return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        }

        if (itemCode == null) {
            return res.throwError("상품이 속한 품목의 코드를 입력해주세요.", "INVALID");
        }

        productInfoNotificationQueryService.getProductInfoNotificationKeys(itemCode);
    }
}
