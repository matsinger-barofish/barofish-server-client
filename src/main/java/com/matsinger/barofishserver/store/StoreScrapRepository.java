package com.matsinger.barofishserver.store;

import com.matsinger.barofishserver.store.object.StoreScrap;
import com.matsinger.barofishserver.store.object.StoreScrapId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreScrapRepository extends JpaRepository<StoreScrap, StoreScrapId> {
    public List<StoreScrap> findByUserId(Integer userId);

    public Boolean existsByStoreIdAndUserId(Integer storeId, Integer userId);
}
