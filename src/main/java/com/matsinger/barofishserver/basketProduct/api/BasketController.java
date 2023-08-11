package com.matsinger.barofishserver.basketProduct.api;

import com.matsinger.barofishserver.basketProduct.application.BasketCommandService;
import com.matsinger.barofishserver.basketProduct.application.BasketQueryService;
import com.matsinger.barofishserver.basketProduct.dto.AddBasketOptionReq;
import com.matsinger.barofishserver.basketProduct.dto.AddBasketReq;
import com.matsinger.barofishserver.basketProduct.dto.BasketProductDto;
import com.matsinger.barofishserver.basketProduct.domain.BasketProductInfo;
import com.matsinger.barofishserver.basketProduct.domain.BasketProductOption;
import com.matsinger.barofishserver.basketProduct.dto.DeleteBasketReq;
import com.matsinger.barofishserver.basketProduct.repository.BasketProductOptionRepository;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.product.application.ProductService;
import com.matsinger.barofishserver.product.optionitem.dto.OptionItemDto;
import com.matsinger.barofishserver.product.domain.Product;
import com.matsinger.barofishserver.store.application.StoreService;
import com.matsinger.barofishserver.store.domain.StoreInfo;
import com.matsinger.barofishserver.utils.Common;
import com.matsinger.barofishserver.utils.CustomResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            List<BasketProductDto> dtos = basketQueryService.selectBasketList(tokenInfo.get().getId());
            res.setData(Optional.ofNullable(dtos));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/list/count")
    public ResponseEntity<CustomResponse<Integer>> countBasket(@RequestHeader(value = "Authorization") Optional<String> auth) {
        CustomResponse<Integer> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Integer count = basketQueryService.countBasketList(tokenInfo.get().getId());
            res.setData(Optional.ofNullable(count));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<CustomResponse<Boolean>> addBasket(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                             @RequestPart(value = "data") AddBasketReq data) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            if (data.getProductId() == null) return res.throwError("상품 아이디를 입력하세요.", "INPUT_CHECK_REQUIRED");
            if (data.getOptions() == null || data.getOptions().size() == 0)
                return res.throwError("상품 옵션을 입력해주세요.", "INPUT_CHECK_REQUIRED");
            Product product = productService.findById(data.getProductId());
            List<BasketProductInfo> infos = new ArrayList<>();
            List<BasketProductDto> productDtos = new ArrayList<>();

            for (AddBasketOptionReq optionReq : data.getOptions()) {
                basketCommandService.processBasketProductAdd(tokenInfo.get().getId(),
                        product.getId(),
                        optionReq.getOptionId(),
                        optionReq.getAmount());
            }
            res.setData(Optional.of(true));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<CustomResponse<BasketProductDto>> updateBasket(@PathVariable("id") Integer id,
                                                                         @RequestHeader(value = "Authorization") Optional<String> auth,
                                                                         @RequestParam(value = "amount") Integer amount) {
        CustomResponse<BasketProductDto> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            if (amount == null) return res.throwError("개수를 입력해주세요.", "INPUT_CHECK_REQUIRED");
            BasketProductInfo info = basketQueryService.selectBasket(id);
            if (tokenInfo.get().getId() != info.getUserId()) return res.throwError("타인의 장바구니 정보입니다.", "NOT_ALLOWED");
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
                    BasketProductDto.builder().product(product.convert2ListDto()).amount(amount).deliveryFee(storeInfo.getDeliverFee()).deliverFeeType(
                            storeInfo.getDeliverFeeType()).minOrderPrice(storeInfo.getMinOrderPrice()).option(optionDto).build();
            res.setData(Optional.ofNullable(dto));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }


    @DeleteMapping("/")
    public ResponseEntity<CustomResponse<Boolean>> deleteBasket(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                @RequestPart(value = "data") DeleteBasketReq data) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            for (Integer basketId : data.getIds()) {
                BasketProductInfo info = basketQueryService.selectBasket(basketId);
                if (tokenInfo.get().getId() != info.getUserId())
                    return res.throwError("타인의 장바구니 정보입니다.", "NOT_ALLOWED");
            }
            basketCommandService.deleteBasket(data.getIds());
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }
}

