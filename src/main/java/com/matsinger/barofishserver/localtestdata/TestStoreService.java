package com.matsinger.barofishserver.localtestdata;

import com.matsinger.barofishserver.store.Store;
import com.matsinger.barofishserver.store.StoreRepository;
import com.matsinger.barofishserver.store.StoreState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TestStoreService {
    private final StoreRepository storeRepository;

    public void createTestStore() {
        for (int i = 1; i < 3; i++) {
            Store createdStore = Store.builder()
                    .state(StoreState.ACTIVE)
                    .loginId("test" + i)
                    .password("test" + i)
                    .joinAt(Timestamp.valueOf(LocalDateTime.now())).build();
            storeRepository.save(createdStore);
        }
    }
}
