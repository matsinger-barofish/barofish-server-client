package com.matsinger.barofishserver.product.option.dto;

import com.matsinger.barofishserver.product.optionitem.dto.OptionItemDto;
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
