package com.matsinger.barofishserver.domain.data.curation.repository;

import com.matsinger.barofishserver.domain.data.curation.domain.Curation;
import com.matsinger.barofishserver.domain.data.curation.domain.CurationState;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CurationRepository extends JpaRepository<Curation, Integer> {
    @Query(value = "SELECT MAX( c.sort_no ) + 1 AS sortNo FROM curation c", nativeQuery = true)
    Tuple selectMaxSortNo();


    List<Curation> findAllByState(CurationState state, Sort sortNo);
}
