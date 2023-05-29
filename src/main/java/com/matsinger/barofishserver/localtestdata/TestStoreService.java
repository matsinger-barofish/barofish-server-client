package com.matsinger.barofishserver.localtestdata;

import com.matsinger.barofishserver.store.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TestStoreService {
    private final StoreRepository storeRepository;
    private final StoreInfoRepository storeInfoRepository;

    private final TestStoreInfoService testStoreInfoService;

    public static final List<String> suffixes = List.of("A", "B");

    public void createTestStore() {

        for (String suffix : suffixes) {
            boolean isStorePresent = storeRepository.findByLoginId("store" + suffix).isPresent();

            if (!isStorePresent) {
                StoreInfo createdStoreInfo = StoreInfo.builder()
                        .backgroudImage("storeInfo" + suffix)
                        .profileImage("storeInfo" + suffix)
                        .name("storeInfo" + suffix)
                        .location("storeInfo" + suffix)
                        .keyword("storeInfo" + suffix).build();

                Store createdStore = Store.builder()
                        .state(StoreState.ACTIVE)
                        .loginId("store" + suffix)
                        .password("store" + suffix)
                        .joinAt(Timestamp.valueOf(LocalDateTime.now())).build();

                createdStoreInfo.setStore(createdStore);

                storeRepository.save(createdStore);
                storeInfoRepository.save(createdStoreInfo);
            }
        }
    }
}
