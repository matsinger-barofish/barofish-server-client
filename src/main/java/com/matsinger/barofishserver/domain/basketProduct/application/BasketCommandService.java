package com.matsinger.barofishserver.domain.basketProduct.application;

import com.matsinger.barofishserver.domain.basketProduct.domain.BasketProductInfo;
import com.matsinger.barofishserver.domain.basketProduct.domain.BasketProductOption;
import com.matsinger.barofishserver.domain.basketProduct.dto.AddBasketOptionReq;
import com.matsinger.barofishserver.domain.basketProduct.dto.AddBasketReq;
import com.matsinger.barofishserver.domain.basketProduct.repository.BasketProductInfoRepository;
import com.matsinger.barofishserver.domain.basketProduct.repository.BasketProductOptionRepository;
import com.matsinger.barofishserver.domain.order.domain.Orders;
import com.matsinger.barofishserver.domain.order.orderprductinfo.domain.OrderProductInfo;
import com.matsinger.barofishserver.domain.product.application.ProductQueryService;
import com.matsinger.barofishserver.domain.product.application.ProductService;
import com.matsinger.barofishserver.domain.product.domain.Product;
import com.matsinger.barofishserver.domain.product.domain.ProductDeliverFeeType;
import com.matsinger.barofishserver.domain.product.option.application.OptionQueryService;
import com.matsinger.barofishserver.domain.product.option.domain.Option;
import com.matsinger.barofishserver.domain.product.optionitem.application.OptionItemQueryService;
import com.matsinger.barofishserver.domain.product.optionitem.domain.OptionItem;
import com.matsinger.barofishserver.domain.product.optionitem.repository.OptionItemRepository;
import com.matsinger.barofishserver.domain.store.application.StoreInfoQueryService;
import com.matsinger.barofishserver.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class BasketCommandService {
    private final BasketProductInfoRepository basketProductInfoRepository;
    private final BasketProductOptionRepository basketProductOptionRepository;
    private final ProductService productService;
    private final OptionItemRepository optionItemRepository;
    private final OptionQueryService optionQueryService;
    private final ProductQueryService productQueryService;
    private final BasketQueryService basketQueryService;
    private final OptionItemQueryService optionItemQueryService;
    private final StoreInfoQueryService storeInfoQueryService;

    public void addProductToBasket(Integer userId,
                                   Integer productId,
                                   Integer optionId,
                                   Integer amount,
                                   Integer deliveryFee) {

        BasketProductInfo
                productInfo =
                BasketProductInfo.builder().userId(userId).productId(productId).amount(amount).deliveryFee(deliveryFee).build();
        productInfo = basketProductInfoRepository.save(productInfo);

        if (optionId != null) {
            BasketProductOption
                    option =
                    BasketProductOption.builder().orderProductId(productInfo.getId()).optionId(optionId).build();
            basketProductOptionRepository.save(option);
        }
    }

    public void updateAmountBasket(Integer basketId, Integer amount) {
        BasketProductInfo info = basketProductInfoRepository.findById(basketId).orElseThrow(() -> {
            throw new Error("장바구니 상품 정보를 찾을 수 없습니다.");
        });
        info.setAmount(amount);
        basketProductInfoRepository.save(info);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteBasket(List<Integer> basketIds) {
        basketProductOptionRepository.deleteAllByOrderProductIdIn(basketIds);
        basketProductInfoRepository.deleteAllByIdIn(basketIds);
    }

    @Transactional
    public void deleteBasket(Integer productId, Integer userId) {
        List<BasketProductInfo> infos = basketProductInfoRepository.findByUserIdAndProductId(userId, productId);
        List<Integer> basketIds = infos.stream().map(BasketProductInfo::getId).toList();
        basketProductOptionRepository.deleteAllByOrderProductIdIn(basketIds);
        basketProductInfoRepository.deleteAllByIdIn(basketIds);
    }

    public void processBasketProductAdd(Integer userId,
                                        Integer productId,
                                        Integer optionItemId,
                                        Integer amount) {
        List<BasketProductInfo> infos = basketProductInfoRepository.findByUserIdAndProductId(userId, productId);
        boolean isExist = false;
        for (BasketProductInfo info : infos) {
            List<BasketProductOption> optionItems = basketProductOptionRepository.findAllByOrderProductId(info.getId());
            for (BasketProductOption optionItem : optionItems) {
                if (optionItem.getOptionId() == optionItemId) {
                    isExist = true;
                    OptionItem findedOptionItem = productService.selectOptionItem(optionItemId);
                    Option option = optionQueryService.findById(findedOptionItem.getOptionId());

                    info.setAmount(info.getAmount() + amount);
                    basketProductInfoRepository.save(info);
                }
            }
        }
        if (!isExist) {
            OptionItem
                    optionItem =
                    optionItemRepository.findById(optionItemId).orElseThrow(() -> new BusinessException(
                            "옵션 아이템 정보를 찾을 수 없습니다."));
            Product product = productService.selectProduct(productId);
            int deliveryFee = 0;
            if (product.getDeliverFeeType().equals(ProductDeliverFeeType.FREE)) deliveryFee = 0;
            else if (product.getDeliverFeeType().equals(ProductDeliverFeeType.FIX))
                deliveryFee = product.getDeliverFee();
            else if (product.getDeliverFeeType().equals(ProductDeliverFeeType.FREE_IF_OVER))
                deliveryFee =
                        product.getMinOrderPrice() == null ||
                                optionItem.getDiscountPrice() * amount >
                                        product.getMinOrderPrice() ? 0 : (product.getDeliverFee() ==
                                null ? 0 : product.getDeliverFee());

            BasketProductInfo
                    info =
                    BasketProductInfo.builder().userId(userId).productId(productId).amount(amount).deliveryFee(
                            deliveryFee).build();
            info = basketProductInfoRepository.save(info);
            basketProductOptionRepository.save(BasketProductOption.builder().orderProductId(info.getId()).optionId(optionItemId).build());
        }
    }

    @Transactional
    public void deleteBasketAfterOrder(Orders order, List<OrderProductInfo> orderProductInfos) {
        List<Integer> deleteBasketIds = new ArrayList<>();
        orderProductInfos.forEach(v -> {
            List<BasketProductInfo>
                    infos =
                    basketProductInfoRepository.findByUserIdAndProductId(order.getUserId(), v.getProductId());
            for (BasketProductInfo info : infos) {
                List<BasketProductOption> options = basketProductOptionRepository.findAllByOrderProductId(info.getId());
                for (BasketProductOption option : options) {
                    if (option.getOptionId() == v.getOptionItemId()) {
                        deleteBasketIds.add(info.getId());
                    }
                }
            }
        });
        deleteBasket(deleteBasketIds);
    }

    @Transactional
    public void processBasketProductAddV2(AddBasketReq request, Integer userId) {
        Product product = productQueryService.findById(request.getProductId());
//        StoreInfo storeInfo = storeInfoQueryService.findByStoreId(product.getStoreId());
//        boolean isSConditional = storeInfo.isConditional();

        boolean isNeeded = false;
        for (AddBasketOptionReq optionReq : request.getOptions()) {
            Optional<List<BasketProductInfo>> optionalBasketProductInfos = basketQueryService.findAllByUserIdAndProductId(userId, product.getId());

            OptionItem optionItem = optionItemQueryService.findById(optionReq.getOptionId());
            // 장바구니에 같은 상품이 있으면
            if (optionalBasketProductInfos.isPresent()) {
                // 장바구니 상품에서 필수 옵션이 있는지 체크
                checkNecessaryOptionExistsInBasket(optionalBasketProductInfos);

                // 같은 옵션 아이디가 있을 때 기존 장바구니 상품에 수량만 더함
                if (optionItem.getId() == optionReq.getOptionId()) {
                    addQuantityToExistingBasketProduct(userId, optionReq);
                }

                // 같은 옵션 아이디가 없으면 장바구니에 새로운 상품으로 추가
                if (optionItem.getId() != optionReq.getOptionId()) {
                    addBasketProduct(userId, optionReq, optionItem, product);
                }
            }

            // 장바구니에 같은 상품이 없으면 새로운 상품으로 추가하면서 필수옵션인지 체크
            if (optionalBasketProductInfos.isEmpty()) {
                isNeeded = addBasketProduct(userId, optionReq, optionItem, product);
            }
        }
        // 새로 추가하려는 상품에 필수옵션이 없으면 예외처리
        if (!isNeeded) {
            throw new BusinessException("필수 옵션을 선택해주세요.");
        }
    }

    private void addQuantityToExistingBasketProduct(Integer userId, AddBasketOptionReq optionReq) {
        BasketProductInfo basketProductInfo = basketQueryService.findByUserIdAndOptionItemId(userId, optionReq.getOptionId()).get();
        basketProductInfo.addQuantity(optionReq.getAmount());
    }

    private boolean addBasketProduct(Integer userId, AddBasketOptionReq optionReq, OptionItem optionItem, Product product) {
        Option option = optionQueryService.findById(optionItem.getOptionId());
        BasketProductInfo.builder()
                .userId(userId)
                .storeId(product.getStoreId())
                .optionId(option.getId())
                .isNeeded(option.getIsNeeded())
                .amount(optionReq.getAmount())
                .build();
        return option.getIsNeeded();
    }

    private static void checkNecessaryOptionExistsInBasket(Optional<List<BasketProductInfo>> optionalBasketProductInfos) {
        List<BasketProductInfo> basketProductInfos = optionalBasketProductInfos.get();
        boolean necessaryOptionExists = basketProductInfos.stream().anyMatch(v -> v.isNeeded());
        if (!necessaryOptionExists) {
            throw new BusinessException("장바구니에 필수옵션이 존재하지 않습니다.");
        }
    }
}
