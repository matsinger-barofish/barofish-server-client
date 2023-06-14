package com.matsinger.barofishserver.filter;

import com.matsinger.barofishserver.category.Category;
import com.matsinger.barofishserver.product.productinfo.*;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Filter {
    private List<Category> categories;

    private List<ProductType> types;

    private List<ProductLocation> locations;

    private List<ProductProcess> processes;

    private List<ProductStorage> storages;

    private List<ProductUsage> usages;
}
