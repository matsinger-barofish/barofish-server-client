package com.matsinger.barofishserver.data.tip;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TipRepository extends JpaRepository<Tip, Integer>, JpaSpecificationExecutor<Tip> {
    List<Tip> findAllByType(TipType type);
}
