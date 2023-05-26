package com.matsinger.barofishserver.compare;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompareSetRepository extends JpaRepository<CompareSet, Integer> {
    public List<CompareSet> findAllByUserId(Integer userId);
}
