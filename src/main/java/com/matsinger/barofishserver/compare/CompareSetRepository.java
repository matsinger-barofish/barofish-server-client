package com.matsinger.barofishserver.compare;

import com.matsinger.barofishserver.compare.obejct.CompareSet;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompareSetRepository extends JpaRepository<CompareSet, Integer> {
    public List<CompareSet> findAllByUserId(Integer userId);

    @Query(value = "SELECT q1.setId as setId\n" +
            "FROM (SELECT ci.compare_set_id AS setId, GROUP_CONCAT( ci.product_id ORDER BY ci.product_id, '' ) AS compareSet\n" +
            "      FROM compare_item ci\n" +
            "      GROUP BY ci.compare_set_id) AS q1\n" +
            "GROUP BY q1.compareSet\n" +
            "ORDER BY COUNT( q1.setId ) DESC\n" +
            "LIMIT 5;", nativeQuery = true)
    public List<Tuple> selectPopularCompareSetIdList();

    @Query(value = "SELECT ci.product_id AS productId, COUNT( ci.product_id ) AS count\n" +
            "      FROM compare_item ci\n" +
            "      GROUP BY ci.product_id\n" +
            "      ORDER BY COUNT( ci.product_id ) DESC", nativeQuery = true)
    public List<Tuple> selectMostComparedProudct();

    @Query(value = "select product_id as pId from compare_item where compare_set_id in (SELECT cs.id\n" +
            "FROM compare_set cs\n" +
            "         JOIN compare_item ci ON cs.id = ci.compare_set_id\n" +
            "WHERE ci.product_id = :productId)\n" +
            "GROUP BY product_id\n" +
//            "ORDER BY rand()\n" +
            "limit 3;", nativeQuery = true)
    public List<Tuple> selectRecommendCompareSet(@Param("productId") Integer productId);
}
