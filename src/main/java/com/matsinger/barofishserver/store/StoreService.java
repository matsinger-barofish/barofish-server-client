package com.matsinger.barofishserver.store;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class StoreService {

    private final StoreRepository storeRepository;

    public Optional<Store> selectStoreOptional(Integer id) {
        try {
            return storeRepository.findById(id);
        } catch (Exception e) {
            return null;
        }
    }
}
