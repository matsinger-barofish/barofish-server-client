package com.matsinger.barofishserver.product.object;

import com.matsinger.barofishserver.category.CategoryDto;
import com.matsinger.barofishserver.inquiry.Inquiry;
import com.matsinger.barofishserver.inquiry.InquiryDto;
import com.matsinger.barofishserver.product.productinfo.*;
import com.matsinger.barofishserver.review.Review;
import com.matsinger.barofishserver.review.ReviewDto;
import com.matsinger.barofishserver.review.ReviewTotalStatistic;
import com.matsinger.barofishserver.store.object.SimpleStore;
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
    ProductState state;
    SimpleStore store;
    CategoryDto category;
    String[] images;
    String title;
    Boolean isLike;
    Integer originPrice;
    Integer discountRate;
    String deliveryInfo;
    Integer deliveryFee;
    String description;
    String[] descriptionImages;
    Integer expectedDeliverDay;
    ProductType type;
    ProductLocation location;
    ProductProcess process;
    ProductUsage usage;
    ProductStorage storage;
    Timestamp createdAt;

    List<ProductListDto> comparedProduct = new ArrayList<>();

    ReviewTotalStatistic reviewStatistics;
    List<ReviewDto> reviews = new ArrayList<>();
    Integer reviewCount;
    List<InquiryDto> inquiries = new ArrayList<>();
}
