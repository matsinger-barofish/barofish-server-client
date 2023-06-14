package com.matsinger.barofishserver.data.tip;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TipRepository  extends JpaRepository<Tip, Integer> {
    List<Tip> findAllByType(TipType type);
}
