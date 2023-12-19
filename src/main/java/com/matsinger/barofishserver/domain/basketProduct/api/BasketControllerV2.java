package com.matsinger.barofishserver.domain.basketProduct.api;

import com.matsinger.barofishserver.domain.basketProduct.application.BasketQueryService;
import com.matsinger.barofishserver.domain.basketProduct.dto.BasketProductDto;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.utils.Common;
import com.matsinger.barofishserver.utils.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

//    @GetMapping("/list")
//    public ResponseEntity<CustomResponse<List<BasketProductDto>>> selectBasketV2(
//            @RequestHeader(value = "Authorization") Optional<String> auth) {
//        CustomResponse<List<BasketProductDto>> res = new CustomResponse<>();
//
//        TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
//
//        List<BasketProductDto> dtos = basketQueryService.selectBasketList(tokenInfo.getId());
//        basketQueryService.selectBasketListV2(tokenInfo.getId());
//
//        res.setData(Optional.ofNullable(dtos));
//        return ResponseEntity.ok(res);
//    }

    @GetMapping("/list")
    public ResponseEntity<CustomResponse<List<BasketProductDto>>> selectBasketV2(
            @RequestHeader(value = "Authorization") Optional<String> auth) {
        CustomResponse<List<BasketProductDto>> res = new CustomResponse<>();

        TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);

        List<BasketProductDto> dtos = basketQueryService.selectBasketList(tokenInfo.getId());
        basketQueryService.selectBasketListV2(tokenInfo.getId());

        res.setData(Optional.ofNullable(dtos));
        return ResponseEntity.ok(res);
    }
}
