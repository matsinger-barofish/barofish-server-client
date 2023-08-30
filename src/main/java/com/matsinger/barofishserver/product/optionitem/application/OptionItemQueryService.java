package com.matsinger.barofishserver.product.optionitem.application;

import com.matsinger.barofishserver.product.optionitem.domain.OptionItem;
import com.matsinger.barofishserver.product.optionitem.repository.OptionItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OptionItemQueryService {

    private final OptionItemRepository optionItemRepository;

    public OptionItem findById(int optionItemId) {
        return optionItemRepository.findById(optionItemId)
                                   .orElseThrow(() -> new IllegalStateException("옵션 아이템 정보를 찾을 수 없습니다."));
    }
}
