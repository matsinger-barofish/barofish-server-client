package com.matsinger.barofishserver.compare.repository;

import com.matsinger.barofishserver.compare.domain.CompareSet;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompareSetRepository extends JpaRepository<CompareSet, Integer> {
    List<CompareSet> findAllByUserId(Integer userId);

    List<CompareSet> findAllByUserIdIn(List<Integer> userIds);

    void deleteAllByUserIdIn(List<Integer> userIds);

    @Query(value = "SELECT q1.setId as setId\n" +
            "FROM (SELECT ci.compare_set_id AS setId, GROUP_CONCAT( ci.product_id ORDER BY ci.product_id, '' ) AS compareSet\n" +
            "      FROM compare_item ci\n" +
            "      GROUP BY ci.compare_set_id) AS q1\n" +
            "GROUP BY q1.compareSet\n" +
            "ORDER BY COUNT( q1.setId ) DESC\n" +
            "LIMIT 5;", nativeQuery = true)
    List<Tuple> selectPopularCompareSetIdList();

    @Query(value = "SELECT cs.id as id\n" +
            "FROM compare_set cs\n" +
            "         JOIN compare_item ci ON cs.id = ci.compare_set_id\n" +
            "WHERE cs.user_id = :userId\n" +
            "  AND ci.product_id IN ( :productIds )\n" +
            "GROUP BY compare_set_id\n" +
            "HAVING COUNT( * ) = 3\n", nativeQuery = true)
    List<Tuple> checkExistHavingSet(@Param(value = "userId") Integer userId,
                                    @Param(value = "productIds") List<Integer> productIds);

    @Query(value = " SELECT cs.*\n" +
            " FROM compare_set cs\n" +
            " WHERE (cs.user_id = :userId AND (cs.id NOT IN (SELECT cs2.id\n" +
            "                                           FROM compare_set cs2\n" +
            "                                                    INNER JOIN compare_item ci ON cs2.id = ci.compare_set_id\n" +
            "                                           WHERE ((NOT ci.product_id IN ( :productIds ))))))", nativeQuery = true)
    Optional<CompareSet> selectHavingSet(@Param(value = "userId") Integer userId,
                                         @Param(value = "productIds") List<Integer> productIds);
}
