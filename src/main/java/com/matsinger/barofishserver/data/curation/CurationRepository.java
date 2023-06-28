package com.matsinger.barofishserver.data.curation;

import com.matsinger.barofishserver.data.curation.object.Curation;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CurationRepository extends JpaRepository<Curation, Integer> {
    @Query(value = "SELECT MAX( c.sort_no ) + 1 AS sortNo FROM curation c", nativeQuery = true)
    Tuple selectMaxSortNo();


}
