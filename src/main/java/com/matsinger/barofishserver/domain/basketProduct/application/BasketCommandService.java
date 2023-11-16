package com.matsinger.barofishserver.domain.basketProduct.application;

import com.matsinger.barofishserver.domain.basketProduct.domain.BasketProductInfo;
import com.matsinger.barofishserver.domain.basketProduct.domain.BasketProductOption;
import com.matsinger.barofishserver.domain.basketProduct.repository.BasketProductInfoRepository;
import com.matsinger.barofishserver.domain.basketProduct.repository.BasketProductOptionRepository;
import com.matsinger.barofishserver.domain.order.domain.Orders;
import com.matsinger.barofishserver.domain.order.orderprductinfo.domain.OrderProductInfo;
import com.matsinger.barofishserver.domain.product.application.ProductService;
import com.matsinger.barofishserver.domain.product.domain.Product;
import com.matsinger.barofishserver.domain.product.domain.ProductDeliverFeeType;
import com.matsinger.barofishserver.domain.product.optionitem.domain.OptionItem;
import com.matsinger.barofishserver.domain.product.optionitem.repository.OptionItemRepository;
import com.matsinger.barofishserver.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class BasketCommandService {
    private final BasketProductInfoRepository infoRepository;
    private final BasketProductOptionRepository optionRepository;
    private final ProductService productService;
    private final OptionItemRepository optionItemRepository;

    public void addProductToBasket(Integer userId,
                                   Integer productId,
                                   Integer optionId,
                                   Integer amount,
                                   Integer deliveryFee) {

        BasketProductInfo
                productInfo =
                BasketProductInfo.builder().userId(userId).productId(productId).amount(amount).deliveryFee(deliveryFee).build();
        productInfo = infoRepository.save(productInfo);

        if (optionId != null) {
            BasketProductOption
                    option =
                    BasketProductOption.builder().orderProductId(productInfo.getId()).optionId(optionId).build();
            optionRepository.save(option);
        }
    }

    public void updateAmountBasket(Integer basketId, Integer amount) {
        BasketProductInfo info = infoRepository.findById(basketId).orElseThrow(() -> {
            throw new Error("장바구니 상품 정보를 찾을 수 없습니다.");
        });
        info.setAmount(amount);
        infoRepository.save(info);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteBasket(List<Integer> basketIds) {
        optionRepository.deleteAllByOrderProductIdIn(basketIds);
        infoRepository.deleteAllByIdIn(basketIds);
    }

    @Transactional
    public void deleteBasket(Integer productId, Integer userId) {
        List<BasketProductInfo> infos = infoRepository.findByUserIdAndProductId(userId, productId);
        List<Integer> basketIds = infos.stream().map(BasketProductInfo::getId).toList();
        optionRepository.deleteAllByOrderProductIdIn(basketIds);
        infoRepository.deleteAllByIdIn(basketIds);
    }

    public void processBasketProductAdd(Integer userId, Integer productId, Integer optionId, Integer amount) {
        List<BasketProductInfo> infos = infoRepository.findByUserIdAndProductId(userId, productId);
        boolean isExist = false;
        for (BasketProductInfo info : infos) {
            List<BasketProductOption> options = optionRepository.findAllByOrderProductId(info.getId());
            for (BasketProductOption option : options) {
                if (option.getOptionId() == optionId) {
                    isExist = true;
                    OptionItem
                            optionItem =
                            productService.selectOptionItem(optionId);
                    if (optionItem.getMaxAvailableAmount() != null &&
                            (info.getAmount() + amount > optionItem.getMaxAvailableAmount()))
                        throw new BusinessException("최대 주문 수량을 초과하였습니다.");

                    info.setAmount(info.getAmount() + amount);
                    infoRepository.save(info);
                }
            }
        }
        if (!isExist) {
            OptionItem
                    optionItem =
                    optionItemRepository.findById(optionId).orElseThrow(() -> new BusinessException(
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
            info = infoRepository.save(info);
            optionRepository.save(BasketProductOption.builder().orderProductId(info.getId()).optionId(optionId).build());
        }
    }

    @Transactional
    public void deleteBasketAfterOrder(Orders order, List<OrderProductInfo> orderProductInfos) {
        List<Integer> deleteBasketIds = new ArrayList<>();
        orderProductInfos.forEach(v -> {
            List<BasketProductInfo>
                    infos =
                    infoRepository.findByUserIdAndProductId(order.getUserId(), v.getProductId());
            for (BasketProductInfo info : infos) {
                List<BasketProductOption> options = optionRepository.findAllByOrderProductId(info.getId());
                for (BasketProductOption option : options) {
                    if (option.getOptionId() == v.getOptionItemId()) {
                        deleteBasketIds.add(info.getId());
                    }
                }
            }
        });
        deleteBasket(deleteBasketIds);
    }
}
