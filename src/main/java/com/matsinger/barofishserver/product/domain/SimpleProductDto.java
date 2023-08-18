package com.matsinger.barofishserver.product.domain;

import com.matsinger.barofishserver.address.domain.Address;
import com.matsinger.barofishserver.category.dto.CategoryDto;
import com.matsinger.barofishserver.compare.filter.dto.CompareFilterDto;
import com.matsinger.barofishserver.inquiry.dto.InquiryDto;
import com.matsinger.barofishserver.product.productfilter.dto.ProductFilterValueDto;
import com.matsinger.barofishserver.product.dto.ProductListDto;
import com.matsinger.barofishserver.review.dto.ReviewDto;
import com.matsinger.barofishserver.review.dto.ReviewTotalStatistic;
import com.matsinger.barofishserver.searchFilter.dto.SearchFilterFieldDto;
import com.matsinger.barofishserver.store.domain.StoreDeliverFeeType;
import com.matsinger.barofishserver.store.dto.SimpleStore;
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
    String[] images;
    String title;
    Integer originPrice;
    String deliveryInfo;
    Integer deliveryFee;
    StoreDeliverFeeType deliverFeeType;
    Integer minOrderPrice;
    String[] descriptionImages;
    Integer representOptionItemId;
    Boolean needTaxation;
    Timestamp createdAt;
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
