package com.matsinger.barofishserver.domain.store.application;

import com.matsinger.barofishserver.domain.store.domain.Store;
import com.matsinger.barofishserver.domain.store.repository.StoreRepository;
import com.matsinger.barofishserver.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoreQueryService {

    private final StoreRepository storeRepository;

    public Store findById(int id) {
        return storeRepository.findById(id)
                .orElseThrow(() -> new BusinessException("파트너를 찾울 수 없습니다."));
    }
}
