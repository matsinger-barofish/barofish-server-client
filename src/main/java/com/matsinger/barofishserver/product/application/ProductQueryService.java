package com.matsinger.barofishserver.product.application;

import com.matsinger.barofishserver.compare.repository.SaveProductRepository;
import com.matsinger.barofishserver.compare.filter.domain.CompareFilter;
import com.matsinger.barofishserver.compare.filter.repository.CompareFilterRepository;
import com.matsinger.barofishserver.compare.domain.SaveProductId;
import com.matsinger.barofishserver.inquiry.domain.Inquiry;
import com.matsinger.barofishserver.inquiry.repository.InquiryRepository;
import com.matsinger.barofishserver.product.domain.Product;
import com.matsinger.barofishserver.product.domain.ProductSortBy;
import com.matsinger.barofishserver.product.domain.SimpleProductDto;
import com.matsinger.barofishserver.product.dto.ProductListDto;
import com.matsinger.barofishserver.product.optionitem.domain.OptionItem;
import com.matsinger.barofishserver.product.optionitem.repository.OptionItemRepository;
import com.matsinger.barofishserver.product.productfilter.application.ProductFilterService;
import com.matsinger.barofishserver.product.productfilter.domain.ProductFilterValue;
import com.matsinger.barofishserver.product.productfilter.dto.ProductFilterValueDto;
import com.matsinger.barofishserver.product.productfilter.repository.ProductFilterRepository;
import com.matsinger.barofishserver.product.repository.ProductRepository;
import com.matsinger.barofishserver.product.repository.ProductRepositoryImpl;
import com.matsinger.barofishserver.review.application.ReviewQueryService;
import com.matsinger.barofishserver.review.repository.ReviewRepository;
import com.matsinger.barofishserver.review.dto.ReviewTotalStatistic;
import com.matsinger.barofishserver.searchFilter.domain.ProductSearchFilterMap;
import com.matsinger.barofishserver.searchFilter.dto.SearchFilterFieldDto;
import com.matsinger.barofishserver.searchFilter.repository.ProductSearchFilterMapRepository;
import com.matsinger.barofishserver.searchFilter.repository.SearchFilterFieldRepository;
import com.matsinger.barofishserver.store.repository.StoreInfoRepository;
import com.matsinger.barofishserver.store.application.StoreService;
import com.matsinger.barofishserver.store.domain.StoreInfo;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductQueryService {

    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    private final OptionItemRepository optionItemRepository;
    private final ProductFilterRepository productFilterRepository;
    private final CompareFilterRepository compareFilterRepository;
    private final StoreInfoRepository storeInfoRepository;
    private final ProductSearchFilterMapRepository productSearchFilterMapRepository;
    private final StoreService storeService;
    private final ReviewQueryService reviewQueryService;
    private final SearchFilterFieldRepository searchFilterFieldRepository;
    private final SaveProductRepository saveProductRepository;
    private final InquiryRepository inquiryRepository;
    private final ProductRepositoryImpl productRepositoryImpl;

    public ProductListDto createProductListDtos(Integer id) {
        Product findProduct = productRepository.findById(id).orElseThrow(() -> {
            throw new Error("상품 정보를 찾을 수 없습니다.");
        });
        int productId = findProduct.getId();
        String productImages = findProduct.getImages();

        StoreInfo storeInfo = storeService.selectStoreInfo(productId);
        Integer reviewCount = reviewRepository.countAllByProductId(productId);
        OptionItem optionItem = optionItemRepository.findById(findProduct.getRepresentOptionItemId())
                .orElseThrow(() -> new Error("옵션 아이템 정보를 찾을 수 없습니다."));

        List<ProductFilterValue> productFilters = productFilterRepository.findAllByProductId(productId);
        List<ProductFilterValueDto> filterValueDtos = getProductFilterValueDtos(productFilters);

        return ProductListDto.builder()
                .id(productId)
                .state(findProduct.getState())
                .image(productImages.substring(1, productImages.length() - 1).split(",")[0])
                .originPrice(optionItem.getOriginPrice())
                .discountPrice(optionItem.getDiscountPrice())
                .title(findProduct.getTitle())
                .reviewCount(reviewCount)
                .storeId(storeInfo.getStoreId())
                .storeName(storeInfo.getName())
                .parentCategoryId(findProduct.getCategoryId())
                .filterValues(filterValueDtos)
                .build();
    }

    @NotNull
    private List<ProductFilterValueDto> getProductFilterValueDtos(List<ProductFilterValue> productFilters) {
        List<ProductFilterValueDto> filterValueDtos = new ArrayList<>();
        for (ProductFilterValue productFilterValue : productFilters) {
            CompareFilter findCompareFilter = compareFilterRepository.findById(productFilterValue.getCompareFilterId())
                    .orElseThrow(() -> new Error("비교하기 항목 정보를 찾을 수 없습니다."));

            ProductFilterValueDto productFilterDto = ProductFilterValueDto.builder()
                    .compareFilterId(findCompareFilter.getId())
                    .compareFilterName(findCompareFilter.getName())
                    .value(productFilterValue.getValue())
                    .build();
            filterValueDtos.add(productFilterDto);
        }
        return filterValueDtos;
    }

    private void setReviewStatistics(int productId, SimpleProductDto productDto) {
        ReviewTotalStatistic reviewStatistics = reviewQueryService.selectReviewTotalStatisticWithProductId(productId);
        productDto.setReviewStatistics(reviewStatistics);
    }

    private void setIsLike(int productId, Integer userId, SimpleProductDto productDto) {
        boolean isLike;
        if (userId == null) {
            isLike = false;
        } else {
            isLike = saveProductRepository.existsById(new SaveProductId(userId, productId));
        }
        productDto.setIsLike(isLike);
    }

    private void setInquiries(int productId, SimpleProductDto productDto) {
        List<Inquiry> inquiries = inquiryRepository.findAllByProductId(productId);
        productDto.setInquiries(inquiries.stream().map(Inquiry::convert2Dto).toList());
    }

    private void setStore(Product product, SimpleProductDto productDto) {
        StoreInfo storeInfo = storeInfoRepository.findById(product.getStoreId())
                .orElseThrow(() -> new Error("상점 정보를 찾을 수 없습니다."));
        productDto.setStore(storeInfo.convert2Dto());
    }

    private void setComparedProducts(int productId, SimpleProductDto productDto) {
        List<Product> comparedProducts = productRepository.selectComparedProductList(productId);
        productDto.setComparedProduct(comparedProducts.stream().map(Product::convert2ListDto).toList());
    }

    private void setOriginAndDiscountPrice(Product product, SimpleProductDto productDto) {
        OptionItem optionItem = optionItemRepository.findById(product.getRepresentOptionItemId())
                .orElseThrow(() -> new Error("옵션 아이템 정보를 찾을 수 없습니다."));

        productDto.setOriginPrice(optionItem.getOriginPrice());
        productDto.setDiscountPrice(optionItem.getDiscountPrice());
    }

    private void setSearchFilterFields(int productId, SimpleProductDto productDto) {
        List<ProductSearchFilterMap> productSearchFilterMaps = productSearchFilterMapRepository
                .findAllByProductId(productId);
        List<SearchFilterFieldDto> searchFilterFieldDtos = productSearchFilterMaps.stream()
                .map(ProductSearchFilterMap::getFieldId)
                .map(
                        (fieldId) -> searchFilterFieldRepository.findById(fieldId)
                                .orElseThrow(() -> new Error("검색 필터 필드 정보를 찾을 수 없습니다."))
                                .convert2Dto())
                .toList();
        productDto.setSearchFilterFields(searchFilterFieldDtos);
    }

    public Product findById(int productId) {
        return productRepository.findById(productId)
                                .orElseThrow(() -> new IllegalArgumentException("상품 정보를 찾을 수 없습니다."));
    }

    public Page<ProductListDto> getPagedProducts(PageRequest pageRequest, ProductSortBy sortBy, int userId) {

        return productRepositoryImpl.getProducts(pageRequest, sortBy, userId);
    }


}
