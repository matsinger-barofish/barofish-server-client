package com.matsinger.barofishserver.store;

import com.matsinger.barofishserver.store.object.Store;
import com.matsinger.barofishserver.store.object.StoreState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Integer> {
    public Optional<Store> findByLoginId(String loginId);

    public List<Store> findAllByStateEquals(StoreState state);

}
