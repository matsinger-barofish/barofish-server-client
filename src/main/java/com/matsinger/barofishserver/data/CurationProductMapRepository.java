package com.matsinger.barofishserver.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CurationProductMapRepository extends JpaRepository<CurationProductMap, Long> {
    public List<CurationProductMap> findAllByCuration_Id (Long curationId);
}
