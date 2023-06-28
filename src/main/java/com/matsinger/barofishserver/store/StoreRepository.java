package com.matsinger.barofishserver.store;

import com.matsinger.barofishserver.store.object.Store;
import com.matsinger.barofishserver.store.object.StoreState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Integer> , JpaSpecificationExecutor<Store> {

    public Optional<Store> findByLoginId(String loginId);

    public Page<Store> findAllByStateEquals(StoreState state, PageRequest pageRequest);

}
