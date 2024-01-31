package com.matsinger.barofishserver.domain.search.application;

import com.matsinger.barofishserver.domain.product.domain.Product;
import com.matsinger.barofishserver.domain.product.dto.ProductListDto;
import com.matsinger.barofishserver.domain.product.repository.ProductRepository;
import com.matsinger.barofishserver.domain.search.domain.SearchKeyword;
import com.matsinger.barofishserver.domain.search.dto.SearchProductDto;
import com.matsinger.barofishserver.domain.search.repository.SearchKeywordQueryRepository;
import com.matsinger.barofishserver.domain.search.repository.SearchKeywordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class SearchKeywordQueryService {
    private final SearchKeywordRepository searchKeywordRepository;
    private final ProductRepository productRepository;
    private SearchKeywordQueryRepository searchKeywordQueryRepository;

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

    public List<SearchProductDto> selectSearchProductTitles(String keyword) {
        List<SearchProductDto> searchProductDtos = searchKeywordQueryRepository.selectSearchKeyword(keyword);
        return null;
    }
}
