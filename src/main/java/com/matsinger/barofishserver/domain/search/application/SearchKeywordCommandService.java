package com.matsinger.barofishserver.domain.search.application;

import com.matsinger.barofishserver.domain.product.repository.ProductRepository;
import com.matsinger.barofishserver.domain.search.repository.SearchKeywordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class SearchKeywordCommandService {
    private final SearchKeywordRepository searchKeywordRepository;
    private final ProductRepository productRepository;

    public void resetRank() {
        List<SearchKeywordRepository.KeywordRank> ranks = searchKeywordRepository.selectRank();
        searchKeywordRepository.deleteAll();
        for (SearchKeywordRepository.KeywordRank rank : ranks) {
            searchKeywordRepository.save(rank.getKeyword(), 0, rank.getRank());
        }
    }
}
