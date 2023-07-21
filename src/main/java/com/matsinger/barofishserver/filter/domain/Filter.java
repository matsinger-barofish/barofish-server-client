package com.matsinger.barofishserver.filter.domain;

import com.matsinger.barofishserver.category.domain.Category;

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
