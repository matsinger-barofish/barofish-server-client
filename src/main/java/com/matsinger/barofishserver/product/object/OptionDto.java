package com.matsinger.barofishserver.product.object;

import com.matsinger.barofishserver.category.CategoryDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class OptionDto {
    Integer id;
    Boolean isNeeded;
    List<OptionItemDto> optionItems;
}
