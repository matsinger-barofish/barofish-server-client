package com.matsinger.barofishserver.store;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreRepository extends JpaRepository<Store, Integer> {
    public Store findByLoginId(String loginId);

    public List<Store> findAllByStateEquals(StoreState state);
}
