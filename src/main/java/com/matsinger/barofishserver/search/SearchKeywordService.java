package com.matsinger.barofishserver.search;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class SearchKeywordService {
    private final SearchKeywordRepository searchKeywordRepository;

    public void searchKeyword(String keyword) {
        SearchKeyword check = searchKeywordRepository.findByKeywordEquals(keyword);
        if (check == null) {
            SearchKeyword searchKeyword = SearchKeyword.builder().keyword(keyword).amount(0).prevRank(null).build();
            searchKeywordRepository.save(keyword, 1, null);
        } else {
            searchKeywordRepository.increaseKeywordAmount(keyword);
        }
    }

    public List<SearchKeyword> selectTopSearchKeywords() {
        return searchKeywordRepository.findTop10ByOrderByAmountDesc();
    }
}
