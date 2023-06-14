package com.matsinger.barofishserver.category;

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
    List<CategoryDto> categories = new ArrayList<>();
}
