package com.matsinger.barofishserver.category.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AddCategoryCompareFilterReq {
    Integer categoryId;
    Integer compareFilterId;
}
