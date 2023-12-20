package com.matsinger.barofishserver.domain.store.application;

import com.matsinger.barofishserver.domain.store.repository.StoreInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoreInfoQueryService {

    private final StoreInfoRepository storeInfoRepository;

}
