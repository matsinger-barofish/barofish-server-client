package com.matsinger.barofishserver.basketProduct.application;

import com.matsinger.barofishserver.basketProduct.domain.BasketProductInfo;
import com.matsinger.barofishserver.basketProduct.domain.BasketProductOption;
import com.matsinger.barofishserver.basketProduct.dto.BasketProductDto;
import com.matsinger.barofishserver.basketProduct.repository.BasketProductInfoRepository;
import com.matsinger.barofishserver.basketProduct.repository.BasketProductOptionRepository;
import com.matsinger.barofishserver.product.application.ProductQueryService;
import com.matsinger.barofishserver.product.application.ProductService;
import com.matsinger.barofishserver.product.domain.Product;
import com.matsinger.barofishserver.product.dto.ProductListDto;
import com.matsinger.barofishserver.product.productfilter.application.ProductFilterService;
import com.matsinger.barofishserver.review.repository.ReviewRepository;
import com.matsinger.barofishserver.store.application.StoreService;
import com.matsinger.barofishserver.store.domain.StoreInfo;
import com.matsinger.barofishserver.store.dto.SimpleStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class BasketQueryService {
    private final BasketProductInfoRepository infoRepository;
    private final BasketProductOptionRepository optionRepository;
    private final com.matsinger.barofishserver.product.optionitem.repository.OptionItemRepository optionItemRepository;
    private final ReviewRepository reviewRepository;
    private final StoreService storeService;
    private final ProductFilterService productFilterService;
    private final ProductService productService;

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
            StoreInfo storeInfo = storeService.selectStoreInfo(product.getStoreId());
            SimpleStore store = storeService.convert2SimpleDto(storeInfo, userId);

            List<BasketProductOption> options = optionRepository.findAllByOrderProductId(info.getId());
            BasketProductOption option = options.size() == 0 ? null : options.get(0);

            com.matsinger.barofishserver.product.optionitem.dto.OptionItemDto optionDto;
            if (option == null) {
                optionDto = null;
            } else {
                com.matsinger.barofishserver.product.optionitem.domain.OptionItem
                        optionItem =
                        optionItemRepository.findById(option.getOptionId()).orElseThrow(() -> new IllegalArgumentException(
                                "옵션 아이템 정보를 찾을 수 없습니다."));
                optionDto = optionItem.convert2Dto();
                optionDto.setDeliverBoxPerAmount(product.getDeliverBoxPerAmount());
            }

            ProductListDto productListDto = createProductListDto(product);
            BasketProductDto
                    basketProductDto =
                    BasketProductDto.builder().id(info.getId()).product(productListDto).amount(info.getAmount())
                            .minOrderPrice(store.getMinOrderPrice()).store(
                            store).option(optionDto).build();
            productDtos.add(basketProductDto);
        }
        return productDtos;
    }

    private ProductListDto createProductListDto(Product product) {
        StoreInfo storeInfo = storeService.selectStoreInfo(product.getStoreId());
        Integer reviewCount = reviewRepository.countAllByProductId(product.getId());
        com.matsinger.barofishserver.product.optionitem.domain.OptionItem
                optionItem =
                optionItemRepository.findById(product.getRepresentOptionItemId()).orElseThrow(() -> new Error(
                        "옵션 아이템 정보를 찾을 수 없습니다."));

        return ProductListDto.builder().id(product.getId()).state(product.getState()).image(product.getImages().substring(
                1,
                product.getImages().length() - 1).split(",")[0]).originPrice(optionItem.getOriginPrice()).discountPrice(
                optionItem.getDiscountPrice()).title(product.getTitle()).reviewCount(reviewCount).storeId(storeInfo.getStoreId()).storeName(
                storeInfo.getName()).parentCategoryId(product.getCategory().getCategoryId()).filterValues(
                productFilterService.selectProductFilterValueListWithProductId(product.getId())).build();
    }
}
