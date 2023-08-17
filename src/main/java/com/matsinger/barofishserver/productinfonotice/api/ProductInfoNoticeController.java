package com.matsinger.barofishserver.productinfonotice.api;

import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.productinfonotice.application.ProductInfoNotificationCommandService;
import com.matsinger.barofishserver.productinfonotice.application.ProductInfoNotificationQueryService;
import com.matsinger.barofishserver.productinfonotice.domain.ProductInformation;
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
    private final ProductInfoNotificationQueryService productInfoNotificationQueryService;
    private final ProductInfoNotificationCommandService productInfoNotificationCommandService;

    @GetMapping("/getForm")
    public ResponseEntity<CustomResponse<Object>> getForm(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                          @RequestParam(value = "itemCode", required = true) String itemCode) {

        CustomResponse<Object> res = new CustomResponse<>();
        Set<TokenAuthType> permission = Set.of(TokenAuthType.ADMIN, TokenAuthType.PARTNER);
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(permission, auth);
        if (tokenInfo == null) {
            return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        }

        if (itemCode == null) {
            return res.throwError("상품이 속한 품목의 코드를 입력해주세요.", "INVALID");
        }
        try {
            ProductInformation productInformation = productInfoNotificationQueryService.getProductInfoNotificationKeys(itemCode);
            res.setData(Optional.ofNullable(productInformation));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<CustomResponse<Object>> create(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                         @RequestBody ProductInformation request) {
        CustomResponse<Object> res = new CustomResponse<>();
        Set<TokenAuthType> permission = Set.of(TokenAuthType.ADMIN, TokenAuthType.PARTNER);
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(permission, auth);
        if (tokenInfo == null) {
            return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        }

        if (request.getItemCode() == null) {
            return res.throwError("상품이 속한 품목의 코드를 입력해주세요.", "INVALID");
        }

        if (request.getProductId() == null) {
            return res.throwError("상품의 아이디를 입력해주세요.", "INVALID");
        }
        try {
            productInfoNotificationCommandService.addProductInfoNotification(request);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }
}
