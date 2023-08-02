package com.matsinger.barofishserver.data.tip.repository;

import com.matsinger.barofishserver.data.tip.domain.TipState;
import com.matsinger.barofishserver.data.tip.domain.TipType;
import com.matsinger.barofishserver.data.tip.domain.Tip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TipRepository extends JpaRepository<Tip, Integer>, JpaSpecificationExecutor<Tip> {
    List<Tip> findAllByTypeAndState(TipType type, TipState state);

    List<Tip> findAllByState(TipState state);

    List<Tip> findAllByIdIn(List<Integer> ids);
}
