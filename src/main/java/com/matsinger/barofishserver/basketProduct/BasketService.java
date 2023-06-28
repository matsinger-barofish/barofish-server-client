package com.matsinger.barofishserver.basketProduct;

import com.matsinger.barofishserver.basketProduct.obejct.BasketProductDto;
import com.matsinger.barofishserver.basketProduct.obejct.BasketProductInfo;
import com.matsinger.barofishserver.basketProduct.obejct.BasketProductOption;
import com.matsinger.barofishserver.product.ProductService;
import com.matsinger.barofishserver.product.object.OptionItem;
import com.matsinger.barofishserver.product.object.OptionItemDto;
import com.matsinger.barofishserver.product.object.Product;
import com.matsinger.barofishserver.store.StoreService;
import com.matsinger.barofishserver.store.object.SimpleStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class BasketService {

    private final BasketProductInfoRepository infoRepository;
    private final BasketProductOptionRepository optionRepository;
    private final StoreService storeService;
    private final ProductService productService;

    public BasketProductInfo selectBasket(Integer id) {
        return infoRepository.findById(id).orElseThrow(() -> {
            throw new Error("장바구니 상품 정보를 찾을 수 없습니다.");
        });

    }

    public List<BasketProductDto> selectBasketList(Integer userId) {
        List<BasketProductInfo> infos = infoRepository.findAllByUserId(userId);
        List<BasketProductDto> productDtos = new ArrayList<>();
        for (BasketProductInfo info : infos) {
            Product product = productService.selectProduct(info.getProductId());
            SimpleStore store = storeService.selectStoreInfo(product.getStoreId()).convert2Dto();
            List<BasketProductOption> options = optionRepository.findAllByOrderProductId(info.getId());
            BasketProductOption option = options.size() == 0 ? null : options.get(0);
            OptionItemDto
                    optionDto =
                    option != null ? productService.selectOptionItem(option.getOptionId()).convert2Dto() : null;
            productDtos.add(BasketProductDto.builder().id(info.getId()).product(productService.convert2ListDto(product)).amount(
                    info.getAmount()).deliveryFee(product.getDeliveryFee()).store(store).option(optionDto).build());
        }
        return productDtos;
    }

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
            OptionItem optionItem = productService.selectOptionItem(optionId);
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
