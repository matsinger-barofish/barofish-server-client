package com.matsinger.barofishserver.domain.basketProduct.api;

import com.matsinger.barofishserver.domain.basketProduct.application.BasketCommandService;
import com.matsinger.barofishserver.domain.basketProduct.application.BasketQueryService;
import com.matsinger.barofishserver.domain.basketProduct.dto.AddBasketOptionReq;
import com.matsinger.barofishserver.domain.basketProduct.dto.AddBasketReq;
import com.matsinger.barofishserver.domain.basketProduct.dto.BasketProductDto;
import com.matsinger.barofishserver.domain.basketProduct.domain.BasketProductInfo;
import com.matsinger.barofishserver.domain.basketProduct.domain.BasketProductOption;
import com.matsinger.barofishserver.domain.basketProduct.dto.DeleteBasketReq;
import com.matsinger.barofishserver.domain.basketProduct.repository.BasketProductOptionRepository;
import com.matsinger.barofishserver.global.error.ErrorCode;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.domain.product.application.ProductService;
import com.matsinger.barofishserver.domain.product.optionitem.dto.OptionItemDto;
import com.matsinger.barofishserver.domain.product.domain.Product;
import com.matsinger.barofishserver.domain.store.application.StoreService;
import com.matsinger.barofishserver.domain.store.domain.StoreInfo;
import com.matsinger.barofishserver.jwt.exception.JwtBusinessException;
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
@RequestMapping("/api/v1/basket")
public class BasketController {

    private final BasketQueryService basketQueryService;
    private final BasketCommandService basketCommandService;
    private final ProductService productService;
    private final StoreService storeService;
    private final Common utils;
    private final JwtService jwt;
    private final BasketProductOptionRepository optionRepository;


    @GetMapping("/list")
    public ResponseEntity<CustomResponse<List<BasketProductDto>>> selectBasket(@RequestHeader(value = "Authorization") Optional<String> auth) {
        CustomResponse<List<BasketProductDto>> res = new CustomResponse<>();

        Integer userId = null;
        if (auth.isEmpty()) {
            throw new JwtBusinessException(ErrorCode.TOKEN_REQUIRED);
        }
        TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth.get());

        List<BasketProductDto> dtos = basketQueryService.selectBasketList(tokenInfo.getId());
        res.setData(Optional.ofNullable(dtos));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/list/count")
    public ResponseEntity<CustomResponse<Integer>> countBasket(@RequestHeader(value = "Authorization") Optional<String> auth) {
        CustomResponse<Integer> res = new CustomResponse<>();

        Integer count = null;

        Integer userId = null;
        if (auth.isEmpty()) {
            throw new JwtBusinessException(ErrorCode.TOKEN_REQUIRED);
        }
        TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth.get());

        res.setData(Optional.ofNullable(count));
        return ResponseEntity.ok(res);
    }

    @PostMapping("/add")
    public ResponseEntity<CustomResponse<Boolean>> addBasket(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                             @RequestPart(value = "data") AddBasketReq data) {
        CustomResponse<Boolean> res = new CustomResponse<>();

        Integer userId = null;
        if (auth.isEmpty()) {
            throw new JwtBusinessException(ErrorCode.TOKEN_REQUIRED);
        }
        TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth.get());

        if (data.getProductId() == null) throw new IllegalArgumentException("상품 아이디를 입력하세요.");
        if (data.getOptions() == null || data.getOptions().size() == 0)
            throw new IllegalArgumentException("상품 옵션을 입력해주세요.");
        Product product = productService.findById(data.getProductId());

        for (AddBasketOptionReq optionReq : data.getOptions()) {
            basketCommandService.processBasketProductAdd(tokenInfo.getId(),
                    product.getId(),
                    optionReq.getOptionId(),
                    optionReq.getAmount());
        }
        res.setData(Optional.of(true));
        return ResponseEntity.ok(res);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<CustomResponse<BasketProductDto>> updateBasket(@PathVariable("id") Integer id,
                                                                         @RequestHeader(value = "Authorization") Optional<String> auth,
                                                                         @RequestParam(value = "amount") Integer amount) {
        CustomResponse<BasketProductDto> res = new CustomResponse<>();

        Integer userId = null;
        if (auth.isEmpty()) {
            throw new JwtBusinessException(ErrorCode.TOKEN_REQUIRED);
        }
        TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth.get());

        if (amount == null) throw new IllegalArgumentException("갯수를 입력해주세요.");
        BasketProductInfo info = basketQueryService.selectBasket(id);
        if (tokenInfo.getId() != info.getUserId()) throw new IllegalArgumentException("타인의 장바구니 정보입니다.");
        basketCommandService.updateAmountBasket(info.getId(), amount);
        Product product = productService.findById(info.getProductId());
        StoreInfo storeInfo = storeService.selectStoreInfo(product.getStoreId());
        BasketProductOption
                option =
                optionRepository.findAllByOrderProductId(info.getId()).size() !=
                        0 ? optionRepository.findAllByOrderProductId(info.getId()).get(0) : null;
        OptionItemDto
                optionDto =
                option != null ? productService.selectOptionItem(option.getOptionId()).convert2Dto() : null;
        BasketProductDto
                dto =
                BasketProductDto.builder().product(product.convert2ListDto()).amount(amount).deliveryFee(product.getDeliverFee()).deliverFeeType(
                        product.getDeliverFeeType()).minOrderPrice(product.getMinOrderPrice()).option(optionDto).build();
        res.setData(Optional.ofNullable(dto));
        return ResponseEntity.ok(res);
    }


    @DeleteMapping("/")
    public ResponseEntity<CustomResponse<Boolean>> deleteBasket(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                @RequestPart(value = "data") DeleteBasketReq data) {
        CustomResponse<Boolean> res = new CustomResponse<>();

        Integer userId = null;
        if (auth.isEmpty()) {
            throw new JwtBusinessException(ErrorCode.TOKEN_REQUIRED);
        }
        TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth.get());

        for (Integer basketId : data.getIds()) {
            BasketProductInfo info = basketQueryService.selectBasket(basketId);
            if (tokenInfo.getId() != info.getUserId())
                throw new IllegalArgumentException("타인의 장바구니 정보입니다.");
        }
        basketCommandService.deleteBasket(data.getIds());
        return ResponseEntity.ok(res);
    }
}

