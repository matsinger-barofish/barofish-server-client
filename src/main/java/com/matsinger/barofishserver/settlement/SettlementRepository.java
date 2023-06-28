package com.matsinger.barofishserver.settlement;

import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SettlementRepository extends JpaRepository<Settlement, Integer>, JpaSpecificationExecutor<Settlement> {
    List<Settlement> findAllByStoreId(Integer storeId);

    List<Settlement> findAllByState(SettlementState state);


    @Query(value = "SELECT IFNULL( SUM( opi.price * opi.amount ), 0 ) AS amount\n" +
            "FROM order_product_info opi\n" +
            "         JOIN product p ON p.id = opi.product_id\n" +
            "WHERE opi.state = 'FINAL_CONFIRM'\n" +
            "   AND (:isSettled is null or opi.is_settled = :isSettled\n)" +
            "  AND (:storeId is null or p.store_id = :storeId)", nativeQuery = true)
    Tuple getNeedSettleAmount(@Param(value = "storeId") Integer storeId, @Param(value = "isSettled") Boolean isSettled);

}
