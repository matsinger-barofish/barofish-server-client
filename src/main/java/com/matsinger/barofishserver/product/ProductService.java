package com.matsinger.barofishserver.product;


import com.matsinger.barofishserver.category.CategoryDto;
import com.matsinger.barofishserver.category.CategoryRepository;
import com.matsinger.barofishserver.inquiry.Inquiry;
import com.matsinger.barofishserver.inquiry.InquiryService;
import com.matsinger.barofishserver.product.object.*;
import com.matsinger.barofishserver.review.Review;
import com.matsinger.barofishserver.review.ReviewService;
import com.matsinger.barofishserver.review.ReviewTotalStatistic;
import com.matsinger.barofishserver.store.StoreService;
import com.matsinger.barofishserver.store.object.StoreInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class ProductService {
    @Autowired
    private final ProductRepository productRepository;

    @Autowired
    private final CategoryRepository categoryRepository;

    private final OptionRepository optionRepository;
    private final OptionItemRepository optionItemRepository;

    private final StoreService storeService;
    private final InquiryService inquiryService;
    private final ReviewService reviewService;

    public List<Product> selectProductList() {
        return productRepository.findAll();
    }

    public List<Product> selectProductListByPartner(Integer storeId) {
        return productRepository.findAllByStoreIdAndStateNot(storeId, ProductState.DELETED);
    }

    public List<Integer> testQuery(List<Integer> ids) {
        return productRepository.testQuery(ids);
    }

    public void deleteOptionItems(Integer optionId) {
        optionItemRepository.deleteAllByOptionId(optionId);
    }

    @Transactional
    public void deleteOptions(Integer productId) {
        List<Integer> optionIds = optionRepository.findAllByProductId(productId).stream().map(option -> {
            return option.getId();
        }).toList();
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

    public List<ProductListDto> selectProductListWithPagination(Integer page,
                                                                Integer take,
                                                                ProductSortBy sortBy,
                                                                List<Integer> categoryIds,
                                                                List<Integer> typeIds,
                                                                List<Integer> locationIds,
                                                                List<Integer> processIds,
                                                                List<Integer> usageIds,
                                                                List<Integer> storageIds) {
        List<Product> products = new ArrayList<>();
        switch (sortBy) {
            case REVIEW:
                products =
                        productRepository.findWithPaginationSortByReview(Pageable.ofSize(take).withPage(page),
                                categoryIds,
                                typeIds,
                                locationIds,
                                processIds,
                                usageIds,
                                storageIds);
                break;
            case NEW:
                products =
                        productRepository.findWithPaginationSortByNewer(Pageable.ofSize(take).withPage(page),
                                categoryIds,
                                typeIds,
                                locationIds,
                                processIds,
                                usageIds,
                                storageIds);
                break;
            case LIKE:
                products =
                        productRepository.findWithPaginationSortByLike(Pageable.ofSize(take).withPage(page),
                                categoryIds,
                                typeIds,
                                locationIds,
                                processIds,
                                usageIds,
                                storageIds);
                break;
            case SALES:
                products =
                        productRepository.findWithPaginationSortByOrder(Pageable.ofSize(take).withPage(page),
                                categoryIds,
                                typeIds,
                                locationIds,
                                processIds,
                                usageIds,
                                storageIds);
                break;
            case RECOMMEND:
                products =
                        productRepository.findWithPaginationSortByRecommend(Pageable.ofSize(take).withPage(page),
                                categoryIds,
                                typeIds,
                                locationIds,
                                processIds,
                                usageIds,
                                storageIds);
                break;
            case LOW_PRICE:
                products =
                        productRepository.findWithPaginationSortByLowPrice(Pageable.ofSize(take).withPage(page),
                                categoryIds,
                                typeIds,
                                locationIds,
                                processIds,
                                usageIds,
                                storageIds);
                break;
            case HIGH_PRICE:
                products =
                        productRepository.findWithPaginationSortByHighPrice(Pageable.ofSize(take).withPage(page),
                                categoryIds,
                                typeIds,
                                locationIds,
                                processIds,
                                usageIds,
                                storageIds);
                break;
        }

        List<ProductListDto> result = new ArrayList<>();
        for (Product product : products) {
            result.add(product.convert2ListDto());
        }
        return result;
    }

    public Option addOption(Option option) {
        return optionRepository.save(option);
    }

    public OptionItem addOptionItem(OptionItem item) {
        return optionItemRepository.save(item);
    }

    public List<Product> selectProductByAdmin() {
        return productRepository.findALlByStateNot(ProductState.DELETED);
    }

    public Product selectProduct(Integer id) {
        return productRepository.findById(id).orElseThrow(() -> {
            throw new Error("상품 정보를 찾을 수 없습니다.");
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

    public SimpleProductDto convert2SimpleDto(Product product, Integer userId) {
        SimpleProductDto productDto = product.convert2SimpleDto();
        List<Inquiry> inquiries = inquiryService.selectInquiryListWithProductId(product.getId());
        List<Review> reviews = reviewService.selectReviewListByProduct(product.getId(), 0, 50).getContent();
        StoreInfo store = storeService.selectStoreInfo(product.getStoreId());
        List<Product> comparedProducts = selectComparedProductList(product.getId());
        ReviewTotalStatistic reviewStatistics = reviewService.selectReviewTotalStatisticWithProductId(product.getId());
        productDto.setReviewStatistics(reviewStatistics);
        CategoryDto category = product.getCategory().convert2Dto();
        productDto.setIsLike(userId != null ? checkLikeProduct(product.getId(), userId) != 0 : false);
        productDto.setCategory(category);
        productDto.setComparedProduct(comparedProducts.stream().map(product1 -> {
            return product1.convert2ListDto();
        }).toList());
        productDto.setStore(store.convert2Dto());
        productDto.setInquiries(inquiries.stream().map(inquiry -> {
            return inquiry.convert2Dto();
        }).toList());
        productDto.setReviews(reviews.stream().map(review -> {
            return review.convert2Dto();
        }).toList());
        productDto.setReviewCount(reviews.size());
        return productDto;

    }

    public List<OptionDto> selectProductOption(Integer productId) {
        List<Option> options = optionRepository.findAllByProductId(productId);
        List<OptionDto> optionDtos = new ArrayList<>();
        for (Option option : options) {
            List<OptionItem> items = optionItemRepository.findAllByOptionId(option.getId());
            List<OptionItemDto> itemDtos = new ArrayList<>();
            for (OptionItem item : items) {
                itemDtos.add(item.convert2Dto());
            }
            OptionDto optionDto = option.convert2Dto();
            optionDto.setOptionItems(itemDtos);
            optionDtos.add(optionDto);
        }
        return optionDtos;
    }

    public List<Product> selectNewerProductList(Integer page,
                                                Integer take,
                                                List<Integer> categoryIds,
                                                List<Integer> typeIds,
                                                List<Integer> locationIds,
                                                List<Integer> processIds,
                                                List<Integer> usageIds,
                                                List<Integer> storageIds) {
        return productRepository.findNewerWithPagination(Pageable.ofSize(take).withPage(page),
                categoryIds,
                typeIds,
                locationIds,
                processIds,
                usageIds,
                storageIds);
    }

    public List<Product> selectDiscountProductList(Integer page,
                                                   Integer take,
                                                   List<Integer> categoryIds,
                                                   List<Integer> typeIds,
                                                   List<Integer> locationIds,
                                                   List<Integer> processIds,
                                                   List<Integer> usageIds,
                                                   List<Integer> storageIds) {
        return productRepository.findDiscountWithPagination(Pageable.ofSize(take).withPage(page),
                categoryIds,
                typeIds,
                locationIds,
                processIds,
                usageIds,
                storageIds);
    }

    public List<Product> selectPopularProductList(Integer page,
                                                  Integer take,
                                                  List<Integer> categoryIds,
                                                  List<Integer> typeIds,
                                                  List<Integer> locationIds,
                                                  List<Integer> processIds,
                                                  List<Integer> usageIds,
                                                  List<Integer> storageIds) {
        return productRepository.findWithPaginationSortByRecommend(Pageable.ofSize(take).withPage(page),
                categoryIds,
                typeIds,
                locationIds,
                processIds,
                usageIds,
                storageIds);
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
}
