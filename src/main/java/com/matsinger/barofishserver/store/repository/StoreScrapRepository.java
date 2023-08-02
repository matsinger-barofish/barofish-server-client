package com.matsinger.barofishserver.store.repository;

import com.matsinger.barofishserver.store.domain.StoreScrap;
import com.matsinger.barofishserver.store.domain.StoreScrapId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreScrapRepository extends JpaRepository<StoreScrap, StoreScrapId> {
    List<StoreScrap> findByUserId(Integer userId);

    Boolean existsByStoreIdAndUserId(Integer storeId, Integer userId);
}
