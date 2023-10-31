package com.matsinger.barofishserver.domain.filter.domain;

import com.matsinger.barofishserver.domain.category.domain.Category;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Filter {
    private List<Category> categories;
}
