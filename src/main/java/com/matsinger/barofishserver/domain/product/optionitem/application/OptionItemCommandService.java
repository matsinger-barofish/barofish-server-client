package com.matsinger.barofishserver.domain.product.optionitem.application;

import com.matsinger.barofishserver.domain.product.optionitem.domain.OptionItem;
import com.matsinger.barofishserver.domain.product.optionitem.repository.OptionItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OptionItemCommandService {

    private final OptionItemQueryService optionItemQueryService;
    private final OptionItemRepository optionItemRepository;


    public void save(OptionItem optionItem) {
        optionItemRepository.save(optionItem);
    }
}
