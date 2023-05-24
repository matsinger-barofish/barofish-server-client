package com.matsinger.barofishserver.search;

import com.matsinger.barofishserver.product.Product;
import com.matsinger.barofishserver.product.ProductService;
import com.matsinger.barofishserver.utils.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/search")
public class SearchKeywordController {
    private final SearchKeywordService searchKeywordService;

    private final ProductService productService;

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
    public ResponseEntity<CustomResponse<List<Product>>> searchProduct(@RequestParam("keyword") String keyword) {
        CustomResponse<List<Product>> res = new CustomResponse<>();
        try {
            searchKeywordService.searchKeyword(keyword);
            List<Product> products = productService.searchProduct(keyword);
            res.setData(Optional.ofNullable(products));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }
}
