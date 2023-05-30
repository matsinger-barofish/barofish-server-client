package com.matsinger.barofishserver.localtestdata;

import com.matsinger.barofishserver.store.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@Transactional
@RequiredArgsConstructor
public class TestStoreService {
    private final StoreRepository storeRepository;
    private final StoreInfoRepository storeInfoRepository;

    private final TestStoreInfoService testStoreInfoService;

    public static final List<String> suffixes = List.of("A", "B");

    public Store createTestStore(int id, String suffix) {

        boolean isStorePresent = storeRepository.findByLoginId("store" + suffix).isPresent();

        if (!isStorePresent) {
            StoreInfo createdStoreInfo = StoreInfo.builder()
                    .id(id)
                    .backgroudImage("storeInfo" + suffix)
                    .profileImage("storeInfo" + suffix)
                    .name("store" + suffix)
                    .location("storeInfo" + suffix)
                    .keyword("storeInfo" + suffix).build();

            Store createdStore = Store.builder()
                    .id(id)
                    .state(StoreState.ACTIVE)
                    .loginId("store" + suffix)
                    .password("store" + suffix)
                    .joinAt(Timestamp.valueOf(LocalDateTime.now())).build();

            createdStoreInfo.setStore(createdStore);

//            Store savedStore = storeRepository.save(createdStore);
            storeInfoRepository.save(createdStoreInfo);
            return createdStore;
        }
        return storeRepository.findByLoginId("store" + suffix).get();
    }
}
