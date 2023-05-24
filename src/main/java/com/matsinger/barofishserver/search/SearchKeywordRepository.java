package com.matsinger.barofishserver.search;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchKeywordRepository extends JpaRepository<SearchKeyword, Integer> {
    List<SearchKeyword> findTop10ByOrderByAmountDesc();

    @Query(value = "update search_keyword sk set amount = sk.amount + 1 where keyword=:keyword", nativeQuery = true)
    void increaseKeywordAmount(@Param("keyword") String keyword);

    SearchKeyword findByKeywordEquals(String keyword);

    @Query(value = "insert into search_keyword (keyword, amount, prev_rank) values (:keyword, :amount, :prevRank)", nativeQuery = true)
    void save(@Param("keyword") String keyword,
                       @Param("amount") Integer amount,
                       @Param("prevRank") Integer prevRank);
}
