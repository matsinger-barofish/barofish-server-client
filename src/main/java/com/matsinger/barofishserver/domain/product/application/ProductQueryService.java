package com.matsinger.barofishserver.domain.product.application;

import com.matsinger.barofishserver.domain.compare.domain.SaveProductId;
import com.matsinger.barofishserver.domain.compare.filter.domain.CompareFilter;
import com.matsinger.barofishserver.domain.compare.filter.repository.CompareFilterRepository;
import com.matsinger.barofishserver.domain.compare.repository.SaveProductRepository;
import com.matsinger.barofishserver.domain.inquiry.domain.Inquiry;
import com.matsinger.barofishserver.domain.inquiry.repository.InquiryRepository;
import com.matsinger.barofishserver.domain.product.domain.Product;
import com.matsinger.barofishserver.domain.product.domain.ProductSortBy;
import com.matsinger.barofishserver.domain.product.domain.SimpleProductDto;
import com.matsinger.barofishserver.domain.product.dto.ExpectedArrivalDateResponse;
import com.matsinger.barofishserver.domain.product.dto.ProductListDto;
import com.matsinger.barofishserver.domain.product.dto.ProductListDtoV2;
import com.matsinger.barofishserver.domain.product.optionitem.domain.OptionItem;
import com.matsinger.barofishserver.domain.product.optionitem.repository.OptionItemRepository;
import com.matsinger.barofishserver.domain.product.productfilter.domain.ProductFilterValue;
import com.matsinger.barofishserver.domain.product.productfilter.dto.ProductFilterValueDto;
import com.matsinger.barofishserver.domain.product.productfilter.repository.ProductFilterRepository;
import com.matsinger.barofishserver.domain.product.repository.ProductRepository;
import com.matsinger.barofishserver.domain.product.repository.ProductRepositoryImpl;
import com.matsinger.barofishserver.domain.product.weeksdate.application.WeeksDateQueryService;
import com.matsinger.barofishserver.domain.product.weeksdate.domain.WeeksDate;
import com.matsinger.barofishserver.domain.product.weeksdate.repository.WeeksDateRepository;
import com.matsinger.barofishserver.domain.review.application.ReviewQueryService;
import com.matsinger.barofishserver.domain.review.dto.ReviewTotalStatistic;
import com.matsinger.barofishserver.domain.review.repository.ReviewRepository;
import com.matsinger.barofishserver.domain.searchFilter.domain.ProductSearchFilterMap;
import com.matsinger.barofishserver.domain.searchFilter.dto.SearchFilterFieldDto;
import com.matsinger.barofishserver.domain.searchFilter.repository.ProductSearchFilterMapRepository;
import com.matsinger.barofishserver.domain.searchFilter.repository.SearchFilterFieldRepository;
import com.matsinger.barofishserver.domain.store.application.StoreService;
import com.matsinger.barofishserver.domain.store.domain.StoreInfo;
import com.matsinger.barofishserver.domain.store.repository.StoreInfoRepository;
import com.matsinger.barofishserver.domain.user.application.UserQueryService;
import com.matsinger.barofishserver.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
    private final SearchFilterFieldRepository searchFilterFieldRepository;
    private final SaveProductRepository saveProductRepository;
    private final InquiryRepository inquiryRepository;
    private final StoreService storeService;
    private final ReviewQueryService reviewQueryService;
    private final ProductRepositoryImpl productRepositoryImpl;
    private final WeeksDateQueryService weekDateQueryService;
    private final WeeksDateRepository weeksDateRepository;
    private final ProductQueryRepository productQueryRepository;
    private final UserQueryService userQueryService;

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
                                .orElseThrow(() -> new BusinessException("상품 정보를 찾을 수 없습니다."));
    }

    public Page<ProductListDtoV2> getPagedProducts(PageRequest pageRequest, ProductSortBy sortBy, String keyword) {

        return productRepositoryImpl.getProducts(pageRequest, sortBy, keyword);
    }


    public ExpectedArrivalDateResponse getExpectedArrivalDate(LocalDateTime now, Integer productId) {
        Product findProduct = findById(productId);
        List<WeeksDate> weeksDatesWithHoliday = weeksDateRepository.findByDateBetween(
                DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now()),
                DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now().plusWeeks(2))
        );

        int calculateExpectedArrivalDate = findProduct.getExpectedDeliverDay();
        if (findProduct.getExpectedDeliverDay() == 1) {
            calculateExpectedArrivalDate = calculateExpectedArrivalDate(now, Integer.valueOf(findProduct.getForwardingTime()), findProduct.getExpectedDeliverDay(), weeksDatesWithHoliday);
        }

        return ExpectedArrivalDateResponse.builder()
                .productExpectedArrivalDate(findProduct.getExpectedDeliverDay())
                .calculatedExpectedArrivalDate(calculateExpectedArrivalDate)
                .build();
    }

    public int calculateExpectedArrivalDate(LocalDateTime now, Integer productForwardingTime, int productExpectedArrivalDate, List<WeeksDate> weeksDatesWithHoliday) {
        LocalTime localTime = LocalTime.of(productForwardingTime, 0, 0);
        LocalDateTime forwardingTime = LocalDateTime.of(LocalDate.now(), localTime);

        boolean isNowBeforeForwardingTime = now.isBefore(forwardingTime);

        int expectedArrivalDate = productExpectedArrivalDate;
        
        boolean isTodayHoliday = weeksDatesWithHoliday.get(0).isDeliveryCompanyHoliday();

        // 오늘이 휴일이 아니면
        if (!isTodayHoliday) {
            // 출고시간 전에 주문했고, 다음날이 휴일이 아니면 배송도착기간은 1일
            if (isNowBeforeForwardingTime) {
                if (!weeksDatesWithHoliday.get(1).isDeliveryCompanyHoliday()) {
                    expectedArrivalDate = 1;
                }
            }

            // 출고시간 이후에 주문하면 +2일 기간에 공휴일이 포함돼 있으면 넘김,
            // 2일 동안 공휴일이 포함돼 있지 않으면 배송 출발
            if (!isNowBeforeForwardingTime) {
                expectedArrivalDate = getExpectedArrivalDate(weeksDatesWithHoliday);
            }
        }

        // 오늘이 휴일인 경우 출고시간에 상관 없이 배송도착기간이 계산됨
        if (isTodayHoliday) {
            expectedArrivalDate = getExpectedArrivalDate(weeksDatesWithHoliday);
        }

        return expectedArrivalDate;
    }

    private int getExpectedArrivalDate(List<WeeksDate> weeksDatesWithHoliday) {
        int expectedArrivalDate = 2;

        int seq = 1;

        boolean isTwoConsecutiveDayContainsHoliday = true;
        while (isTwoConsecutiveDayContainsHoliday) {
            boolean isOneDayLatterHoliday = weeksDatesWithHoliday.get(seq).isDeliveryCompanyHoliday();
            boolean isTwoDayLatterHoliday = weeksDatesWithHoliday.get(seq + 1).isDeliveryCompanyHoliday();

            if (!isOneDayLatterHoliday && !isTwoDayLatterHoliday) {
                isTwoConsecutiveDayContainsHoliday = false;
                break;
            }
            seq++;
            expectedArrivalDate++;
        }

        if (expectedArrivalDate >= 10) {
            return -1;
        }
        return expectedArrivalDate;
    }

    public Page<ProductListDto> selectTopBarProductList(Integer topBarId,
                                                        PageRequest pageRequest,
                                                        List<Integer> filterFieldsIds,
                                                        List<Integer> categoryIds) {
        PageImpl<ProductListDto> productDtos = null;
        if (topBarId == 1) {
            productDtos = productQueryRepository.selectNewerProducts(
                    pageRequest, categoryIds,
                    filterFieldsIds, null,
                    null, null
            );
        }
        if (topBarId == 2) {
            productDtos = productQueryRepository.selectPopularProducts(
                    pageRequest, categoryIds,
                    filterFieldsIds, null,
                    null, null
            );
        }
        if (topBarId == 3) {
            productDtos = productQueryRepository.selectDiscountProducts(
                    pageRequest, categoryIds,
                    filterFieldsIds, null,
                    null, null
            );
        }

        for (ProductListDto productDto : productDtos) {
            String firstImage = productDto.getImage().split(", ")[0];
            productDto.setImage(firstImage);
        }

        return productDtos;
    }

    public Integer countTopBarProduct(Integer topBarId,
                                      List<Integer> filterFieldsIds,
                                      List<Integer> categoryIds) {
        if (topBarId == 1) {
            return productQueryRepository.countNewerProducts(
                    categoryIds, filterFieldsIds, null,
                    null, null
            );
        }
        if (topBarId == 2) {
            return productQueryRepository.countPopularProducts(
                    categoryIds, filterFieldsIds, null,
                    null, null
            );
        }
        if (topBarId == 3) {
            return productQueryRepository.countDiscountProducts(
                    categoryIds, filterFieldsIds, null,
                    null, null
            );
        }
        throw new BusinessException("탑바를 찾을 수 없습니다.");
    }

}
