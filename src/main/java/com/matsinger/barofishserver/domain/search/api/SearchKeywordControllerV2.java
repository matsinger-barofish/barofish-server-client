package com.matsinger.barofishserver.domain.search.api;

import com.matsinger.barofishserver.domain.search.application.SearchKeywordQueryService;
import com.matsinger.barofishserver.domain.search.dto.SearchProductDto;
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
@RequestMapping("/api/v2/search")
public class SearchKeywordControllerV2 {
    private final SearchKeywordQueryService searchKeywordQueryService;

    @GetMapping("/direct")
    public ResponseEntity<CustomResponse<List<SearchProductDto>>> searchingProductDirectV2(@RequestParam("keyword") String keyword) {
        CustomResponse<List<SearchProductDto>> res = new CustomResponse<>();

        List<SearchProductDto> searchProductDtos = searchKeywordQueryService.selectSearchProductTitles(keyword);

        res.setData(Optional.of(searchProductDtos));
        return ResponseEntity.ok(res);
    }
}
