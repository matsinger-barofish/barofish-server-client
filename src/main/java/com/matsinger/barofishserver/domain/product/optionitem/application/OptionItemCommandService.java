package com.matsinger.barofishserver.domain.product.optionitem.application;

import com.matsinger.barofishserver.domain.product.optionitem.domain.OptionItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OptionItemCommandService {

    private final OptionItemQueryService optionItemQueryService;


    public OptionItem reduceQuantity(Integer optionId, Integer quantity) {
        OptionItem findedOptionItem = optionItemQueryService.findById(optionId);
        findedOptionItem.reduceAmount(quantity);
        return findedOptionItem;
    }
}
