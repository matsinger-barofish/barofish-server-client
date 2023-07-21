package com.matsinger.barofishserver.store.repository;

import com.matsinger.barofishserver.store.domain.Store;
import com.matsinger.barofishserver.store.domain.StoreState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Integer>, JpaSpecificationExecutor<Store> {

    Optional<Store> findByLoginId(String loginId);

    Page<Store> findAllByStateEquals(StoreState state, PageRequest pageRequest);

}
