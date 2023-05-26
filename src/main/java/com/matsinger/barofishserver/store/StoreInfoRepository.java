package com.matsinger.barofishserver.store;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreInfoRepository extends JpaRepository<StoreInfo, Integer> {
    public List<StoreInfo> findAllByStoreIdIn(List<Integer> ids);

}
