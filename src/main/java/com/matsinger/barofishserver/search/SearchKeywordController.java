package com.matsinger.barofishserver.search;

import com.matsinger.barofishserver.product.object.Product;
import com.matsinger.barofishserver.product.ProductService;
import com.matsinger.barofishserver.product.object.ProductListDto;
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
    private final SearchKeywordService searchKeywordService;

    private final ProductService productService;

    private final S3Uploader s3;


    @GetMapping("/rank")
    public ResponseEntity<CustomResponse<List<SearchKeyword>>> selectTopSearchKeywords() {
        CustomResponse<List<SearchKeyword>> res = new CustomResponse<>();
        try {
            List<SearchKeyword> keywords = searchKeywordService.selectTopSearchKeywords();
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
            searchKeywordService.searchKeyword(keyword);
            List<ProductListDto> products = searchKeywordService.searchKeyword(keyword, page - 1, take);
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
            List<SearchKeywordRepository.SearchProduct> result = searchKeywordService.selectSearchProductTitle(keyword);
            List<SearchProductDto> dtos = new ArrayList<>();
            for (SearchKeywordRepository.SearchProduct data : result) {
                dtos.add(SearchProductDto.builder().id(data.getId()).title(data.getTitle()).build());
            }
            res.setData(Optional.ofNullable(dtos));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }
}
