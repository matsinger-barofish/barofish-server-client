package com.matsinger.barofishserver.domain.data.curation.dto;

import com.matsinger.barofishserver.domain.data.curation.domain.CurationState;
import com.matsinger.barofishserver.domain.data.curation.domain.CurationType;
import com.matsinger.barofishserver.domain.product.dto.ProductListDto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class CurationDto {

    private Integer id;
    private String image;
    private String shortName;
    private String title;
    private String description;
    private CurationType type;
    private Integer sortNo;
    private CurationState state;
    private List<ProductListDto> products;
}
