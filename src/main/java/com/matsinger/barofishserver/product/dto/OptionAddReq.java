package com.matsinger.barofishserver.product.dto;

import com.matsinger.barofishserver.product.api.ProductController;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OptionAddReq {

    private Boolean isNeeded;
    private List<OptionItemAddReq> items;
}
