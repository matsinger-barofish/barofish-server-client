package com.matsinger.barofishserver.product;


import com.matsinger.barofishserver.basketProduct.BasketProductOptionRepository;
import com.matsinger.barofishserver.category.CategoryDto;
import com.matsinger.barofishserver.category.CategoryFilterService;
import com.matsinger.barofishserver.compare.SaveProductRepository;
import com.matsinger.barofishserver.compare.obejct.SaveProductId;
import com.matsinger.barofishserver.inquiry.Inquiry;
import com.matsinger.barofishserver.inquiry.InquiryService;
import com.matsinger.barofishserver.product.filter.ProductFilterService;
import com.matsinger.barofishserver.product.object.*;
import com.matsinger.barofishserver.review.object.Review;
import com.matsinger.barofishserver.review.ReviewService;
import com.matsinger.barofishserver.review.object.ReviewTotalStatistic;
import com.matsinger.barofishserver.searchFilter.SearchFilterService;
import com.matsinger.barofishserver.searchFilter.object.ProductSearchFilterMap;
import com.matsinger.barofishserver.searchFilter.object.SearchFilterFieldDto;
import com.matsinger.barofishserver.searchFilter.repository.ProductSearchFilterMapRepository;
import com.matsinger.barofishserver.store.StoreService;
import com.matsinger.barofishserver.store.object.StoreInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryFilterService categoryFilterService;


    private final OptionRepository optionRepository;
    private final OptionItemRepository optionItemRepository;
    private final BasketProductOptionRepository basketProductOptionRepository;
    private final SaveProductRepository saveProductRepository;
    private final ProductFilterService productFilterService;

    private final StoreService storeService;
    private final InquiryService inquiryService;
    private final ReviewService reviewService;
    private final ProductSearchFilterMapRepository productSearchFilterMapRepository;
    private final SearchFilterService searchFilterService;

    public List<Product> selectProductListWithIds(List<Integer> ids) {
        return productRepository.findAllByIdIn(ids);
    }

    public List<Product> selectProductList() {
        return productRepository.findAll();
    }


    public List<Integer> testQuery(List<Integer> ids) {
        return productRepository.testQuery(ids);
    }

    @Transactional
    public void deleteOption(Integer optionId) {
        Option option = selectOption(optionId);
        List<OptionItem> optionItems = optionItemRepository.findAllByOptionIdAndState(optionId, OptionItemState.ACTIVE);
        basketProductOptionRepository.deleteAllByOptionIdIn(optionItems.stream().map(OptionItem::getOptionId).toList());
        optionItems.forEach(v -> {
            v.setState(OptionItemState.DELETED);
        });
        optionItemRepository.saveAll(optionItems);
        option.setState(OptionState.DELETED);
        optionRepository.save(option);
    }

    public void deleteOptionItems(Integer optionId) {
        optionItemRepository.deleteAllByOptionId(optionId);
    }

    public void deleteOptionItem(Integer optionItemId) {
        basketProductOptionRepository.deleteAllByOptionId(optionItemId);
        OptionItem optionItem = selectOptionItem(optionItemId);
        optionItem.setState(OptionItemState.DELETED);
        optionItemRepository.save(optionItem);
    }

    @Transactional
    public void deleteOptions(Integer productId) {
        List<Integer>
                optionIds =
                optionRepository.findAllByProductIdAndState(productId,
                        OptionState.ACTIVE).stream().map(Option::getId).toList();
        for (Integer id : optionIds) {
            deleteOptionItems(id);
        }
        optionRepository.deleteAllByProductId(productId);
    }

    public OptionItem selectOptionItem(Integer optionItemId) {
        return optionItemRepository.findById(optionItemId).orElseThrow(() -> {
            throw new Error("옵션 아이템 정보를 찾을 수 없습니다.");
        });
    }

    public List<Product> selectProductOtherCustomerBuy(List<Integer> productIds) {
        return productRepository.selectProductOtherCustomerBuy(productIds);
    }

    public Page<ProductListDto> selectProductListWithPagination(Integer page,
                                                                Integer take,
                                                                ProductSortBy sortBy,
                                                                List<Integer> categoryIds,
                                                                List<Integer> filterFieldIds,
                                                                Integer curationId,
                                                                String keyword,
                                                                Integer storeId) {
        Page<Product> products;
        switch (sortBy) {
            case REVIEW:
                products =
                        productRepository.findWithPaginationSortByReview(Pageable.ofSize(take).withPage(page),
                                categoryIds,
                                filterFieldIds,
                                curationId,
                                keyword,
                                storeId);
                break;
            case NEW:
                products =
                        productRepository.findWithPaginationSortByNewer(Pageable.ofSize(take).withPage(page),
                                categoryIds,
                                filterFieldIds,
                                curationId,
                                keyword,
                                storeId);
                break;
            case LIKE:
                products =
                        productRepository.findWithPaginationSortByLike(Pageable.ofSize(take).withPage(page),
                                categoryIds,
                                filterFieldIds,
                                curationId,
                                keyword,
                                storeId);
                break;
            case SALES:
                products =
                        productRepository.findWithPaginationSortByOrder(Pageable.ofSize(take).withPage(page),
                                categoryIds,
                                filterFieldIds,
                                curationId,
                                keyword,
                                storeId);
                break;
            case RECOMMEND:
                products =
                        productRepository.findWithPaginationSortByRecommend(Pageable.ofSize(take).withPage(page),
                                categoryIds,
                                filterFieldIds,
                                curationId,
                                keyword,
                                storeId);
                break;
            case LOW_PRICE:
                products =
                        productRepository.findWithPaginationSortByLowPrice(Pageable.ofSize(take).withPage(page),
                                categoryIds,
                                filterFieldIds,
                                curationId,
                                keyword,
                                storeId);
                break;
            default:
                products =
                        productRepository.findWithPaginationSortByHighPrice(Pageable.ofSize(take).withPage(page),
                                categoryIds,
                                filterFieldIds,
                                curationId,
                                keyword,
                                storeId);
                break;
        }

        return products.map(this::convert2ListDto);
    }

    public Option addOption(Option option) {
        return optionRepository.save(option);
    }

    public OptionItem addOptionItem(OptionItem item) {
        return optionItemRepository.save(item);
    }

    public Page<Product> selectProductByAdmin(Pageable pageRequest, Specification<Product> spec) {
        return productRepository.findAll(spec, pageRequest);
    }

    public Product selectProduct(Integer id) {
        return productRepository.findById(id).orElseThrow(() -> {
            throw new Error("상품 정보를 찾을 수 없습니다.");
        });
    }

    public Option selectOption(Integer id) {
        return optionRepository.findById(id).orElseThrow(() -> {
            throw new Error("상품 옵션 정보를 찾을 수 없습니다.");
        });
    }

    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    public Product update(Integer id, Product data) {
        productRepository.findById(id).orElseThrow(() -> {
            throw new Error("상품 정보를 찾을 수 없습니다.");
        });
        return productRepository.save(data);
    }

    public List<Product> searchProduct(String keyword) {
        return productRepository.findByTitleContainsAndStateEquals(keyword, ProductState.ACTIVE);
    }

    public List<Product> selectProductListWithStoreIdAndStateActive(Integer storeId) {
        return productRepository.findByStoreIdAndStateEquals(storeId, ProductState.ACTIVE);
    }

    public List<Product> selectComparedProductList(Integer productId) {
        return productRepository.selectComparedProductList(productId);
    }

    public ProductListDto convert2ListDto(Product product) {
        StoreInfo storeInfo = storeService.selectStoreInfo(product.getStoreId());
        Integer reviewCount = reviewService.countReviewWithProductId(product.getId());
        OptionItem optionItem = selectOptionItem(product.getRepresentOptionItemId());
        return ProductListDto.builder().id(product.getId()).state(product.getState()).image(product.getImages().substring(
                1,
                product.getImages().length() - 1).split(",")[0]).originPrice(optionItem.getOriginPrice()).discountPrice(
                optionItem.getDiscountPrice()).title(product.getTitle()).reviewCount(reviewCount).storeId(storeInfo.getStoreId()).storeName(
                storeInfo.getName()).parentCategoryId(product.getCategory().getCategoryId()).filterValues(
                productFilterService.selectProductFilterValueListWithProductId(product.getId())).build();
    }

    public SimpleProductDto convert2SimpleDto(Product product, Integer userId) {
        SimpleProductDto productDto = product.convert2SimpleDto();
        List<Inquiry> inquiries = inquiryService.selectInquiryListWithProductId(product.getId());
        List<Review>
                reviews =
                reviewService.selectReviewListByProduct(product.getId(), PageRequest.of(0, 50)).getContent();
        StoreInfo store = storeService.selectStoreInfo(product.getStoreId());
        List<Product> comparedProducts = selectComparedProductList(product.getId());
        ReviewTotalStatistic reviewStatistics = reviewService.selectReviewTotalStatisticWithProductId(product.getId());
        productDto.setReviewStatistics(reviewStatistics);
        CategoryDto category = product.getCategory().convert2Dto();
        List<SearchFilterFieldDto>
                searchFilterFields =
                productSearchFilterMapRepository.findAllByProductId(product.getId()).stream().map(ProductSearchFilterMap::getFieldId).map(
                        v -> searchFilterService.selectSearchFilterField(v).convert2Dto()).toList();
        OptionItem optionItem = selectOptionItem(product.getRepresentOptionItemId());
        productDto.setIsLike(userId != null &&
                saveProductRepository.existsById(new SaveProductId(userId, product.getId())));
        productDto.setSearchFilterFields(searchFilterFields);
        productDto.setOriginPrice(optionItem.getOriginPrice());
        productDto.setDiscountPrice(optionItem.getDiscountPrice());
        productDto.setCategory(category);
        productDto.setComparedProduct(comparedProducts.stream().map(Product::convert2ListDto).toList());
        productDto.setStore(storeService.convert2SimpleDto(store, userId));
        productDto.setInquiries(inquiries.stream().map(Inquiry::convert2Dto).toList());
        productDto.setReviews(reviews.stream().map(Review::convert2Dto).toList());
        productDto.setFilterValues(productFilterService.selectProductFilterValueListWithProductId(product.getId()));
        productDto.setReviewCount(reviews.size());
        productDto.setNeedTaxation(product.getNeedTaxation());
        return productDto;

    }


    public List<OptionDto> selectProductOption(Integer productId) {
        List<Option> options = optionRepository.findAllByProductIdAndState(productId, OptionState.ACTIVE);

        return options.stream().map(this::convert2OptionDto).toList();
    }

    public Page<Product> selectNewerProductList(Integer page,
                                                Integer take,
                                                List<Integer> categoryIds,
                                                List<Integer> filterFieldIds) {
        return productRepository.findNewerWithPagination(Pageable.ofSize(take).withPage(page),
                categoryIds,
                filterFieldIds,
                null);
    }

    public Page<Product> selectDiscountProductList(Integer page,
                                                   Integer take,
                                                   List<Integer> categoryIds,
                                                   List<Integer> filterFieldIds) {
        return productRepository.findDiscountWithPagination(Pageable.ofSize(take).withPage(page),
                categoryIds,
                filterFieldIds,
                null);
    }

    public Page<Product> selectPopularProductList(Integer page,
                                                  Integer take,
                                                  List<Integer> categoryIds,
                                                  List<Integer> filterFieldIds) {
        System.out.println((categoryIds != null ? categoryIds.size() : null) +
                " | " +
                (filterFieldIds != null ? filterFieldIds.size() : null));
        return productRepository.findWithPaginationSortByOrder(Pageable.ofSize(take).withPage(page),
                categoryIds,
                filterFieldIds,
                null,
                null,
                null);
    }

    public void likeProduct(Integer productId, Integer userId) {
        productRepository.likeProduct(productId, userId);
    }

    public void unlikeProduct(Integer productId, Integer userId) {
        productRepository.unlikeProduct(productId, userId);
    }

    public Integer checkLikeProduct(Integer productId, Integer userId) {
        if (userId == null) return 0;
        return productRepository.checkLikeProduct(productId, userId);
    }

    public boolean checkExistProductSearchFilterMap(Integer productId, Integer fieldId) {
        return productSearchFilterMapRepository.existsByFieldIdAndProductId(fieldId, productId);
    }

    public void addProductSearchFilters(List<ProductSearchFilterMap> filterMaps) {
        productSearchFilterMapRepository.saveAll(filterMaps);
    }

    public void updateProducts(List<Product> products) {
        productRepository.saveAll(products);
    }

    @Transactional
    public void deleteProductSearchFilters(Integer productId) {
        productSearchFilterMapRepository.deleteAllByProductId(productId);
    }

    public OptionDto convert2OptionDto(Option option) {
        OptionDto optionDto = option.convert2Dto();
        List<OptionItem>
                optionItems =
                optionItemRepository.findAllByOptionIdAndState(option.getId(), OptionItemState.ACTIVE);
        List<OptionItemDto> itemDtos = optionItems.stream().map(OptionItem::convert2Dto).toList();
        optionDto.setOptionItems(itemDtos);
        return optionDto;
    }
}
