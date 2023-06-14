package com.matsinger.barofishserver.search;

import com.matsinger.barofishserver.product.ProductRepository;
import com.matsinger.barofishserver.product.object.Product;
import com.matsinger.barofishserver.product.object.ProductListDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class SearchKeywordService {
    private final SearchKeywordRepository searchKeywordRepository;
    private final ProductRepository productRepository;

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

    public void resetRank() {
        List<SearchKeywordRepository.KeywordRank> ranks = searchKeywordRepository.selectRank();
        searchKeywordRepository.deleteAll();
        for (SearchKeywordRepository.KeywordRank rank : ranks) {
            searchKeywordRepository.save(rank.getKeyword(), 0, rank.getRank());
        }
    }

    public List<SearchKeywordRepository.SearchProduct> selectSearchProductTitle(String keyword) {
        return searchKeywordRepository.selectProductTitle(keyword);
    }

    public List<ProductListDto> searchKeyword(String keyword, Integer page, Integer take) {
        List<Product>
                products =
                productRepository.searchProductList(keyword, Pageable.ofSize(take).withPage(page));
        List<ProductListDto> productListDtos = new ArrayList<>();
        for (Product product:products){
            productListDtos.add(product.convert2ListDto());
        }
        return productListDtos;
    }

}
