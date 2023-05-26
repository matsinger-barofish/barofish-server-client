package com.matsinger.barofishserver.store;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreScrapRepository extends JpaRepository<StoreScrap, StoreScrapId> {
    public List<StoreScrap> findByUserId(Integer userId);
}
