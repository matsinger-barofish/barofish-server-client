package com.matsinger.barofishserver.domain.product.option.application;

import com.matsinger.barofishserver.domain.product.option.domain.Option;
import com.matsinger.barofishserver.domain.product.option.repository.OptionRepository;
import com.matsinger.barofishserver.global.exception.BusinessException;
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
                               .orElseThrow(() -> new BusinessException("옵션 정보를 찾을 수 없습니다."));
    }
}
