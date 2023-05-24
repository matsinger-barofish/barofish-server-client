package com.matsinger.barofishserver.localtestdata;

import com.matsinger.barofishserver.store.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TestStoreService {
    private final StoreRepository storeRepository;
    private final StoreInfoRepository storeInfoRepository;

    private final TestStoreInfoService testStoreInfoService;

    public void createTestStore() {
        for (int i = 1; i < 3; i++) {

            StoreInfo createdStoreInfo = StoreInfo.builder()
                    .backgroudImage("test" + i)
                    .profileImage("test" + i)
                    .name("testStoreInfo" + i)
                    .location("test" + i)
                    .keyword("test" + i).build();

            Store createdStore = Store.builder()
                    .state(StoreState.ACTIVE)
                    .loginId("test" + i)
                    .password("test" + i)
                    .joinAt(Timestamp.valueOf(LocalDateTime.now())).build();

            createdStoreInfo.setStore(createdStore);

            storeRepository.save(createdStore);
            storeInfoRepository.save(createdStoreInfo);
        }
    }
}
