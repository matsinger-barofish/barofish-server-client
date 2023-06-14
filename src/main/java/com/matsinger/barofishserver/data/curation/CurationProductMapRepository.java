package com.matsinger.barofishserver.data.curation;

import com.matsinger.barofishserver.data.curation.object.CurationProductMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CurationProductMapRepository extends JpaRepository<CurationProductMap, Integer> {
    public List<CurationProductMap> findAllByCuration_Id(Integer curationId);

    @Query(value = "delete from curation_product_map WHERE curation_id = :curationId and product_id in (:productIds);", nativeQuery = true)
    public void deleteAllByProductIdIn(@Param("curationId") Integer curationId,
                                       @Param("productIds") List<Integer> productIds);
}
