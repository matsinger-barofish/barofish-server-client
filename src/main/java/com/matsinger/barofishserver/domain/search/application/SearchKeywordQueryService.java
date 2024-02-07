package com.matsinger.barofishserver.domain.search.application;

import com.matsinger.barofishserver.domain.product.domain.Product;
import com.matsinger.barofishserver.domain.product.dto.ProductListDto;
import com.matsinger.barofishserver.domain.product.repository.ProductRepository;
import com.matsinger.barofishserver.domain.search.domain.SearchKeyword;
import com.matsinger.barofishserver.domain.search.dto.SearchDirectResponse;
import com.matsinger.barofishserver.domain.search.dto.SearchProductDto;
import com.matsinger.barofishserver.domain.search.repository.SearchKeywordQueryRepository;
import com.matsinger.barofishserver.domain.search.repository.SearchKeywordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Slf4j
@Service
public class SearchKeywordQueryService {
    private final SearchKeywordRepository searchKeywordRepository;
    private final ProductRepository productRepository;
    private final SearchKeywordQueryRepository searchKeywordQueryRepository;

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

    public List<SearchKeywordRepository.KeywordRank> selectKeywordRank() {
        return searchKeywordRepository.selectRank();
    }

    public List<SearchKeywordRepository.SearchProduct> selectSearchProductTitle(String keyword) {
        return searchKeywordRepository.selectProductTitle(keyword);
    }

    public List<ProductListDto> searchKeyword(String keyword, Integer page, Integer take) {
        List<Product> products = productRepository.searchProductList(keyword, Pageable.ofSize(take).withPage(page));
        List<ProductListDto> productListDtos = new ArrayList<>();
        for (Product product : products) {
            productListDtos.add(product.convert2ListDto());
        }
        return productListDtos;
    }

    public SearchDirectResponse selectSearchProductTitles(String keyword) {
        String convertedKeyword = keyword.replace("\\s+", " "); // 여러개의 공백을 공백 하나로
        String[] keywords = convertedKeyword.split(" ");
        List<SearchProductDto> searchProductDtos = searchKeywordQueryRepository.selectSearchKeyword(keywords);

        // keyword 와 searchProductDto.title에서 매칭되는 단어 수가 가장 많은 것 순으로 정렬
        String nonSpaceKeyword = convertedKeyword.replace(" ", "");
        Map<Integer, List<SearchProductDto>> matchWordCountMap = new HashMap<>();

        for (SearchProductDto searchProductDto : searchProductDtos) {
            int matchingCnt = 0;
            for (char productChar : searchProductDto.getTitle().toCharArray()) {

                for (char word : nonSpaceKeyword.toCharArray()) {
                    if (productChar == word) {
                        matchingCnt++;
                    }
                }
            }
            List<SearchProductDto> existingList = matchWordCountMap.getOrDefault(matchingCnt, new ArrayList<>());
            existingList.add(searchProductDto);
            matchWordCountMap.put(matchingCnt, existingList);
        }

        List<Integer> keySet = new ArrayList<>(matchWordCountMap.keySet());
        Collections.sort(keySet, Collections.reverseOrder());
        List<SearchProductDto> sortedByMatchingCnt = new ArrayList<>();
        for (Integer key : keySet) {
            sortedByMatchingCnt.addAll(matchWordCountMap.get(key));
        }

        List<Integer> productIds = sortedByMatchingCnt.stream()
                .map(v -> v.getId()).toList();

        return new SearchDirectResponse(productIds, sortedByMatchingCnt);
    }
}
