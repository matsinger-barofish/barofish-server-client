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
    void save(@Param("keyword") String keyword, @Param("amount") Integer amount, @Param("prevRank") Integer prevRank);

    @Query(value = "update search_keyword set prev_rank = NULL", nativeQuery = true)
    void resetRank();

    interface KeywordRank {
        Integer getRank();

        String getKeyword();
    }

    @Query(value = "select RANK() over (ORDER BY amount DESC) as 'rank', sk.keyword from search_keyword sk", nativeQuery = true)
    List<KeywordRank> selectRank();


    interface SearchProduct {
        Integer getId();

        String getTitle();
    }

    @Query(value = "select id, title from product where instr(title, :keyword) > 0 and state = \'ACTIVE\'", nativeQuery = true)
    List<SearchProduct> selectProductTitle(String keyword);

}
