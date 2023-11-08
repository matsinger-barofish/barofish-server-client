package com.matsinger.barofishserver.domain.basketProduct.application;

import com.matsinger.barofishserver.domain.basketProduct.domain.BasketProductInfo;
import com.matsinger.barofishserver.domain.basketProduct.domain.BasketProductOption;
import com.matsinger.barofishserver.domain.basketProduct.dto.BasketProductDto;
import com.matsinger.barofishserver.domain.basketProduct.repository.BasketProductInfoRepository;
import com.matsinger.barofishserver.domain.basketProduct.repository.BasketProductOptionRepository;
import com.matsinger.barofishserver.domain.product.application.ProductQueryService;
import com.matsinger.barofishserver.domain.product.domain.ProductState;
import com.matsinger.barofishserver.domain.product.optionitem.domain.OptionItem;
import com.matsinger.barofishserver.domain.product.optionitem.dto.OptionItemDto;
import com.matsinger.barofishserver.domain.product.optionitem.repository.OptionItemRepository;
import com.matsinger.barofishserver.domain.product.application.ProductService;
import com.matsinger.barofishserver.domain.product.domain.Product;
import com.matsinger.barofishserver.domain.product.dto.ProductListDto;
import com.matsinger.barofishserver.domain.product.productfilter.application.ProductFilterService;
import com.matsinger.barofishserver.domain.review.repository.ReviewRepository;
import com.matsinger.barofishserver.domain.store.application.StoreService;
import com.matsinger.barofishserver.domain.store.domain.StoreInfo;
import com.matsinger.barofishserver.domain.store.dto.SimpleStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class BasketQueryService {
    private final BasketProductInfoRepository infoRepository;
    private final BasketProductOptionRepository optionRepository;
    private final OptionItemRepository optionItemRepository;
    private final ReviewRepository reviewRepository;
    private final StoreService storeService;
    private final ProductFilterService productFilterService;
    private final ProductService productService;
    private final ProductQueryService productQueryService;

    public BasketProductInfo selectBasket(Integer id) {
        return infoRepository.findById(id).orElseThrow(() -> {
            throw new Error("장바구니 상품 정보를 찾을 수 없습니다.");
        });

    }

    public Integer countBasketList(Integer userId) {
        List<BasketProductInfo> infos = infoRepository.findAllByUserId(userId);
        return infos.size();
    }

    public List<BasketProductDto> selectBasketList(Integer userId) {
        List<BasketProductInfo> infos = infoRepository.findAllByUserId(userId);
        List<BasketProductDto> productDtos = new ArrayList<>();
        for (BasketProductInfo info : infos) {

            Product product = productService.selectProduct(info.getProductId());

            Boolean isProductExists = productQueryService.existsByIdAndState(product.getId(), ProductState.ACTIVE);
            if (!isProductExists) {
                continue;
            }

            StoreInfo storeInfo = storeService.selectStoreInfo(product.getStoreId());
            SimpleStore store = storeService.convert2SimpleDto(storeInfo, userId);

            List<BasketProductOption> options = optionRepository.findAllByOrderProductId(info.getId());
            BasketProductOption option = options.size() == 0 ? null : options.get(0);

            OptionItemDto optionDto;
            if (option == null) {
                optionDto = null;
            } else {
                OptionItem
                        optionItem =
                        optionItemRepository.findById(option.getOptionId()).orElseThrow(() -> new IllegalArgumentException(
                                "옵션 아이템 정보를 찾을 수 없습니다."));
                optionDto = optionItem.convert2Dto();
                optionDto.setDeliverBoxPerAmount(product.getDeliverBoxPerAmount());
                optionDto.setPointRate(product.getPointRate());
            }

            ProductListDto productListDto = createProductListDto(product);
            BasketProductDto
                    basketProductDto =
                    BasketProductDto.builder().id(info.getId()).product(productListDto).amount(info.getAmount()).deliveryFee(
                            product.getDeliverFee()).deliverFeeType(product.getDeliverFeeType()).minOrderPrice(product.getMinOrderPrice()).store(
                            store).option(optionDto).build();
            productDtos.add(basketProductDto);
        }
        return productDtos;
    }

    private ProductListDto createProductListDto(Product product) {
        StoreInfo storeInfo = storeService.selectStoreInfo(product.getStoreId());
        Integer reviewCount = reviewRepository.countAllByProductId(product.getId());
        OptionItem
                optionItem =
                optionItemRepository.findById(product.getRepresentOptionItemId()).orElseThrow(() -> new Error(
                        "옵션 아이템 정보를 찾을 수 없습니다."));

        return ProductListDto.builder().id(product.getId()).state(product.getState()).image(product.getImages().substring(
                1,
                product.getImages().length() - 1).split(",")[0]).originPrice(optionItem.getOriginPrice()).discountPrice(
                optionItem.getDiscountPrice()).title(product.getTitle()).reviewCount(reviewCount).storeId(storeInfo.getStoreId()).storeName(
                storeInfo.getName()).parentCategoryId(product.getCategory() !=
                null ? product.getCategory().getCategoryId() : null).filterValues(productFilterService.selectProductFilterValueListWithProductId(
                product.getId())).build();
    }
}
