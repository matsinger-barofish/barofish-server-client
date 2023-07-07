package com.matsinger.barofishserver.filter;

import com.matsinger.barofishserver.category.Category;
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
