package com.matsinger.barofishserver.domain.product.domain;

import com.matsinger.barofishserver.domain.address.domain.Address;
import com.matsinger.barofishserver.domain.category.dto.CategoryDto;
import com.matsinger.barofishserver.domain.compare.filter.dto.CompareFilterDto;
import com.matsinger.barofishserver.domain.inquiry.dto.InquiryDto;
import com.matsinger.barofishserver.domain.review.dto.ReviewDto;
import com.matsinger.barofishserver.domain.review.dto.ReviewTotalStatistic;
import com.matsinger.barofishserver.domain.store.dto.SimpleStore;
import com.matsinger.barofishserver.domain.product.productfilter.dto.ProductFilterValueDto;
import com.matsinger.barofishserver.domain.product.dto.ProductListDto;
import com.matsinger.barofishserver.domain.searchFilter.dto.SearchFilterFieldDto;
import lombok.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleProductDto {
    Integer id;
    CategoryDto category;
    ProductState state;
    Integer expectedDeliverDay;
    String forwardingTime;
    String[] images;
    String title;
    Integer originPrice;
    String deliveryInfo;
    Integer deliveryFee;
    ProductDeliverFeeType deliverFeeType;
    Integer minOrderPrice;
    String[] descriptionImages;
    Integer representOptionItemId;
    Boolean needTaxation;
    Timestamp createdAt;
    Timestamp promotionStartAt;
    Timestamp promotionEndAt;
    String description;
    Integer deliverBoxPerAmount;
    Float pointRate;

    // 위는 product 자체의 정보, 아래는 주입받아야 하는 정보
    SimpleStore store;
    Boolean isLike;
    Integer discountPrice;
    Integer reviewCount;
    ReviewTotalStatistic reviewStatistics;

    List<CompareFilterDto> compareFilters;
    List<ProductFilterValueDto> filterValues;
    List<SearchFilterFieldDto> searchFilterFields;

    List<Address> difficultDeliverAddresses;

    @Builder.Default
    List<ProductListDto> comparedProduct = new ArrayList<>();

    @Builder.Default
    List<ReviewDto> reviews = new ArrayList<>();
    @Builder.Default
    List<InquiryDto> inquiries = new ArrayList<>();

}
