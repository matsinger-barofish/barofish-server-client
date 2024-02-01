package com.matsinger.barofishserver.domain.basketProduct.api;

import com.matsinger.barofishserver.domain.basketProduct.application.BasketCommandService;
import com.matsinger.barofishserver.domain.basketProduct.application.BasketQueryService;
import com.matsinger.barofishserver.domain.basketProduct.domain.BasketProductInfo;
import com.matsinger.barofishserver.domain.basketProduct.dto.AddBasketReq;
import com.matsinger.barofishserver.domain.basketProduct.dto.BasketProductDtoV2;
import com.matsinger.barofishserver.domain.basketProduct.dto.DeleteBasketReq;
import com.matsinger.barofishserver.global.exception.BusinessException;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.utils.Common;
import com.matsinger.barofishserver.utils.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/basket")
public class BasketControllerV2 {

    private final Common utils;
    private final JwtService jwt;
    private final BasketQueryService basketQueryService;
    private final BasketCommandService basketCommandService;

    @PostMapping("/add")
    public ResponseEntity<CustomResponse<Boolean>> addBasketV2(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                               @RequestPart(value = "data") AddBasketReq data) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);

        if (data.getProductId() == null) {
            throw new BusinessException("상품 아이디를 입력하세요.");
        }
        if (data.getOptions() == null || data.getOptions().size() == 0) {
            throw new BusinessException("상품 옵션을 입력해주세요.");
        }

        basketCommandService.processBasketProductAddV2(data, tokenInfo.getId());

        res.setData(Optional.of(true));
        return ResponseEntity.ok(res);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<CustomResponse<Boolean>> updateBasketV2(@PathVariable("id") Integer id,
                                                                         @RequestHeader(value = "Authorization") Optional<String> auth,
                                                                         @RequestParam(value = "amount") Integer amount) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);

        if (amount == null) {
            throw new BusinessException("갯수를 입력해주세요.");
        }
        basketCommandService.addAmount(tokenInfo.getId(), id, amount);

        res.setIsSuccess(true);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/list")
    public ResponseEntity<CustomResponse<List<BasketProductDtoV2>>> selectBasketV2(@RequestHeader(value = "Authorization") Optional<String> auth) {
        CustomResponse<List<BasketProductDtoV2>> res = new CustomResponse<>();

        Integer userId = null;
        TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);

        List<BasketProductDtoV2> response = basketQueryService.selectBasketListV2(tokenInfo.getId());
        res.setData(Optional.ofNullable(response));
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/")
    public ResponseEntity<CustomResponse<Boolean>> deleteBasketV2(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                @RequestPart(value = "data") DeleteBasketReq data) {
        CustomResponse<Boolean> res = new CustomResponse<>();

        TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);

        for (Integer basketId : data.getIds()) {
            BasketProductInfo info = basketQueryService.selectBasket(basketId);
            if (tokenInfo.getId() != info.getUserId())
                throw new BusinessException("타인의 장바구니 정보입니다.");
        }
        basketCommandService.deleteBasketV2(tokenInfo.getId(), data.getIds());
        return ResponseEntity.ok(res);
    }
}
