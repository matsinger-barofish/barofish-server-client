package com.matsinger.barofishserver.product.option.application;

import com.matsinger.barofishserver.product.option.domain.Option;
import com.matsinger.barofishserver.product.option.repository.OptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OptionQueryService {

    private final OptionRepository optionRepository;

    public Option findById(int optionId) {
        return optionRepository.findById(optionId)
                .orElseThrow(() -> new IllegalArgumentException("옵션 정보를 찾을 수 없습니다."));
    }
}
