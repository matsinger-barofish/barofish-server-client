package com.matsinger.barofishserver.basketProduct;

import com.matsinger.barofishserver.basketProduct.obejct.BasketProductDto;
import com.matsinger.barofishserver.basketProduct.obejct.BasketProductInfo;
import com.matsinger.barofishserver.basketProduct.obejct.BasketProductOption;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.product.ProductService;
import com.matsinger.barofishserver.product.object.OptionItemDto;
import com.matsinger.barofishserver.product.object.Product;
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

    private final BasketService basketService;
    private final ProductService productService;
    private final Common utils;
    private final JwtService jwt;
    private final BasketProductOptionRepository optionRepository;


    @GetMapping("/list")
    public ResponseEntity<CustomResponse<List<BasketProductDto>>> selectBasket(@RequestHeader(value = "Authorization") Optional<String> auth) {
        CustomResponse<List<BasketProductDto>> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            List<BasketProductDto> dtos = basketService.selectBasketList(tokenInfo.get().getId());
            res.setData(Optional.ofNullable(dtos));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }


    @Getter
    @NoArgsConstructor
    private static class AddBasketOptionReq {
        private Integer optionId;
        private Integer amount;
    }

    @Getter
    @NoArgsConstructor
    private static class AddBasketReq {
        private Integer productId;
        private List<AddBasketOptionReq> options;
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
            Product product = productService.selectProduct(data.getProductId());
            List<BasketProductInfo> infos = new ArrayList<>();
            List<BasketProductDto> productDtos = new ArrayList<>();

            for (AddBasketOptionReq optionReq : data.getOptions()) {
                basketService.processBasketProductAdd(tokenInfo.get().getId(),
                        product.getId(),
                        optionReq.optionId,
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
            BasketProductInfo info = basketService.selectBasket(id);
            if (tokenInfo.get().getId() != info.getUserId()) return res.throwError("타인의 장바구니 정보입니다.", "NOT_ALLOWED");
            basketService.updateAmountBasket(info.getId(), amount);
            Product product = productService.selectProduct(info.getProductId());
            BasketProductOption
                    option =
                    optionRepository.findAllByOrderProductId(info.getId()).size() !=
                            0 ? optionRepository.findAllByOrderProductId(info.getId()).get(0) : null;
            OptionItemDto
                    optionDto =
                    option != null ? productService.selectOptionItem(option.getOptionId()).convert2Dto() : null;
            BasketProductDto
                    dto =
                    BasketProductDto.builder().product(product.convert2ListDto()).amount(amount).deliveryFee(product.getDeliveryFee()).option(
                            optionDto).build();
            res.setData(Optional.ofNullable(dto));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @Getter
    @NoArgsConstructor
    private static class DeleteBasketReq {
        List<Integer> ids;
    }

    @DeleteMapping("/")
    public ResponseEntity<CustomResponse<Boolean>> deleteBasket(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                @RequestPart(value = "data") DeleteBasketReq data) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            for (Integer basketId : data.getIds()) {
                BasketProductInfo info = basketService.selectBasket(basketId);
                if (tokenInfo.get().getId() != info.getUserId())
                    return res.throwError("타인의 장바구니 정보입니다.", "NOT_ALLOWED");
            }
            basketService.deleteBasket(data.getIds());
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }
}

