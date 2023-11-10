package com.matsinger.barofishserver.domain.productinfonotice.api;

import com.matsinger.barofishserver.global.error.ErrorCode;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.domain.productinfonotice.application.ProductInfoNotificationCommandService;
import com.matsinger.barofishserver.domain.productinfonotice.application.ProductInfoNotificationQueryService;
import com.matsinger.barofishserver.domain.productinfonotice.domain.ProductInformation;
import com.matsinger.barofishserver.jwt.exception.JwtBusinessException;
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
        if (auth.isEmpty()) throw new JwtBusinessException(ErrorCode.NOT_ALLOWED);
        TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN, TokenAuthType.PARTNER, TokenAuthType.ALLOW), auth.get());

        if (itemCode == null) {
            throw new IllegalArgumentException("상품이 속한 품목의 코드를 입력해주세요.");
        }

        ProductInformation
                productInformation =
                productInfoNotificationQueryService.getProductInfoNotificationForm(itemCode);
        res.setData(Optional.ofNullable(productInformation));
        return ResponseEntity.ok(res);
    }

    @PostMapping("/add")
    public ResponseEntity<CustomResponse<Object>> create(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                         @RequestBody ProductInformation request) {
        CustomResponse<Object> res = new CustomResponse<>();

        if (auth.isEmpty()) throw new JwtBusinessException(ErrorCode.NOT_ALLOWED);;
        TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN, TokenAuthType.PARTNER), auth.get());

        if (request.getItemCode() == null) {
            throw new IllegalArgumentException("상품정보제공고시 품목 코드를 입력해주세요.");
        }

        if (request.getProductId() == null) {
            throw new IllegalArgumentException("상품의 아이디를 입력해주세요.");
        }
        productInfoNotificationCommandService.addProductInfoNotification(request);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/update")
    public ResponseEntity<CustomResponse<Object>> update(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                         @RequestBody ProductInformation request) {
        CustomResponse<Object> res = new CustomResponse<>();

        if (auth.isEmpty()) throw new JwtBusinessException(ErrorCode.NOT_ALLOWED);;
        TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN, TokenAuthType.PARTNER), auth.get());

        if (request.getItemCode() == null) {
            throw new IllegalArgumentException("상품정보제공고시 품목 코드를 입력해주세요.");
        }

        if (request.getProductId() == null) {
            throw new IllegalArgumentException("상품의 아이디를 입력해주세요.");
        }
        productInfoNotificationCommandService.updateProductInfoNotification(request);

        return ResponseEntity.ok(res);
    }

    @GetMapping("/get/{productId}")
    public ResponseEntity<CustomResponse<Object>> get(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                      @PathVariable("productId") int productId) {
        CustomResponse<Object> res = new CustomResponse<>();

        if (auth.isEmpty()) throw new JwtBusinessException(ErrorCode.NOT_ALLOWED);;
        jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ALLOW), auth.get());

        ProductInformation
                productInfoNotification =
                productInfoNotificationQueryService.getProductInfoNotification(productId);
        res.setData(Optional.ofNullable(productInfoNotification));

        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<CustomResponse<Object>> delete(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                         @PathVariable("productId") int productId) {
        CustomResponse<Object> res = new CustomResponse<>();

        if (auth.isEmpty()) throw new JwtBusinessException(ErrorCode.NOT_ALLOWED);;
        TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN, TokenAuthType.PARTNER), auth.get());

        productInfoNotificationCommandService.deleteProductInfoNotification(productId);

        return ResponseEntity.ok(res);
    }
}
