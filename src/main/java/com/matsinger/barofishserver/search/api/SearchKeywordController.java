package com.matsinger.barofishserver.search.api;

import com.matsinger.barofishserver.product.application.ProductService;
import com.matsinger.barofishserver.product.dto.ProductListDto;
import com.matsinger.barofishserver.search.application.SearchKeywordCommandService;
import com.matsinger.barofishserver.search.application.SearchKeywordQueryService;
import com.matsinger.barofishserver.search.repository.SearchKeywordRepository;
import com.matsinger.barofishserver.search.application.SearchKeywordService;
import com.matsinger.barofishserver.search.dto.SearchProductDto;
import com.matsinger.barofishserver.search.domain.SearchKeyword;
import com.matsinger.barofishserver.utils.CustomResponse;
import com.matsinger.barofishserver.utils.S3.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/search")
public class SearchKeywordController {
    private final SearchKeywordQueryService searchKeywordQueryService;


    @GetMapping("/rank")
    public ResponseEntity<CustomResponse<List<SearchKeyword>>> selectTopSearchKeywords() {
        CustomResponse<List<SearchKeyword>> res = new CustomResponse<>();
        try {
            List<SearchKeyword> keywords = searchKeywordQueryService.selectTopSearchKeywords();
            res.setData(Optional.ofNullable(keywords));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("")
    public ResponseEntity<CustomResponse<List<ProductListDto>>> searchProduct(@RequestParam("keyword") String keyword,
                                                                              @RequestParam(value = "page", defaultValue = "1", required = false) Integer page,
                                                                              @RequestParam(value = "take", defaultValue = "10", required = false) Integer take) {
        CustomResponse<List<ProductListDto>> res = new CustomResponse<>();
        try {
            searchKeywordQueryService.searchKeyword(keyword);
            List<ProductListDto> products = searchKeywordQueryService.searchKeyword(keyword, page - 1, take);
            res.setData(Optional.ofNullable(products));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/direct")
    public ResponseEntity<CustomResponse<List<SearchProductDto>>> searchingProductDirect(@RequestParam("keyword") String keyword) {
        CustomResponse<List<SearchProductDto>> res = new CustomResponse<>();
        try {
            List<SearchKeywordRepository.SearchProduct>
                    result =
                    searchKeywordQueryService.selectSearchProductTitle(keyword);
            List<SearchProductDto> dtos = new ArrayList<>();
            for (SearchKeywordRepository.SearchProduct data : result) {
                dtos.add(SearchProductDto.builder().id(data.getId()).title(data.getTitle()).build());
            }
            res.setData(Optional.of(dtos));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }
}
