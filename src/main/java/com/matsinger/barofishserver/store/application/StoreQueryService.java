package com.matsinger.barofishserver.store.application;

import com.matsinger.barofishserver.store.domain.Store;
import com.matsinger.barofishserver.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoreQueryService {

    private final StoreRepository storeRepository;

    public Store findById(int id) {
        return storeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("파트너를 찾울 수 없습니다."));
    }
}
