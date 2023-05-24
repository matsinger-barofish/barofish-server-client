package com.matsinger.barofishserver.localtestdata;

import com.matsinger.barofishserver.store.StoreInfo;
import com.matsinger.barofishserver.store.StoreInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestStoreInfoService {

    private final StoreInfoRepository storeInfoRepository;

    public void createStoreInfo() {
        for (int i = 1; i < 3; i++) {
            StoreInfo createdStoreInfo = StoreInfo.builder()
                    .backgroudImage("test" + i)
                    .profileImage("test" + i)
                    .name("testStoreInfo" + i)
                    .location("test" + i)
                    .keyword("test" + i).build();
            storeInfoRepository.save(createdStoreInfo);
        }
    }
}
