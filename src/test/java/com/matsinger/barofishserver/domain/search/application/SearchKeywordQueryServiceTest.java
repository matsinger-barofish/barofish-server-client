package com.matsinger.barofishserver.domain.search.application;

import com.matsinger.barofishserver.domain.search.dto.SearchProductDto;
import com.matsinger.barofishserver.domain.search.repository.SearchKeywordQueryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest
@ActiveProfiles("local")
class SearchKeywordQueryServiceTest {

    @Autowired private SearchKeywordQueryRepository searchKeywordQueryRepository;
    @Autowired private SearchKeywordQueryService searchKeywordQueryService;

    @DisplayName("")
    @Test
    void test() {
        // given
        String keyword = "바로수산 반건조 장어";
        List<SearchProductDto> searchProductDtos = searchKeywordQueryService.selectSearchProductTitles(keyword);
        // when

        // then
    }
}