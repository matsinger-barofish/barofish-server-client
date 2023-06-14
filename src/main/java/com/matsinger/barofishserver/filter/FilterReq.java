package com.matsinger.barofishserver.filter;

import com.matsinger.barofishserver.category.Category;
import com.matsinger.barofishserver.product.productinfo.*;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class FilterReq {
    private List<Integer> categoryIds;
    private List<Integer> typeIds;
    private List<Integer> locationIds;
    private List<Integer> processIds;
    private List<Integer> storageIds;
    private List<Integer> usageIds;
}
