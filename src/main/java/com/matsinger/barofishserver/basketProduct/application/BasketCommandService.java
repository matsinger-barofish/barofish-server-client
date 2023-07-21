package com.matsinger.barofishserver.basketProduct.application;

import com.matsinger.barofishserver.basketProduct.domain.BasketProductInfo;
import com.matsinger.barofishserver.basketProduct.domain.BasketProductOption;
import com.matsinger.barofishserver.basketProduct.repository.BasketProductInfoRepository;
import com.matsinger.barofishserver.basketProduct.repository.BasketProductOptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class BasketCommandService {
    private final BasketProductInfoRepository infoRepository;
    private final BasketProductOptionRepository optionRepository;
    private final com.matsinger.barofishserver.product.optionitem.repository.OptionItemRepository optionItemRepository;

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


    public void processBasketProductAdd(Integer userId, Integer productId, Integer optionId, Integer amount) {
        List<BasketProductInfo> infos = infoRepository.findByUserIdAndProductId(userId, productId);
        boolean isExist = false;
        for (BasketProductInfo info : infos) {
            List<BasketProductOption> options = optionRepository.findAllByOrderProductId(info.getId());
            for (BasketProductOption option : options) {
                if (option.getOptionId() == optionId) {
                    isExist = true;
                    info.setAmount(info.getAmount() + amount);
                    infoRepository.save(info);
                }
            }
        }
        if (!isExist) {
            com.matsinger.barofishserver.product.optionitem.domain.OptionItem
                    optionItem =
                    optionItemRepository.findById(optionId).orElseThrow(() -> new IllegalArgumentException(
                            "옵션 아이템 정보를 찾을 수 없습니다."));
            int
                    deliveryFee =
                    optionItem.getDeliverFee() *
                            ((int) (amount /
                                    (optionItem.getDeliverBoxPerAmount() ==
                                            null ? 9999999 : optionItem.getDeliverBoxPerAmount())) + 1);
            BasketProductInfo
                    info =
                    BasketProductInfo.builder().userId(userId).productId(productId).amount(amount).deliveryFee(
                            deliveryFee).build();
            info = infoRepository.save(info);
            optionRepository.save(BasketProductOption.builder().orderProductId(info.getId()).optionId(optionId).build());
        }
    }
}
