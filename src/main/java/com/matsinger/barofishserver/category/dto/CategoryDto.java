package com.matsinger.barofishserver.category.dto;

import com.matsinger.barofishserver.compare.filter.dto.CompareFilterDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    Integer id;
    Integer parentId;
    String ParentCategoryName;
    String image;
    String name;
    @Builder.Default
    List<CategoryDto> categories = new ArrayList<>();
    @Builder.Default
    List<CompareFilterDto> filters = new ArrayList<>();
}
