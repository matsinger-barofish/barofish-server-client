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

    @DisplayName("")
    @Test
    void test() {
        // given
        String keyword = "장어 반건조";
        List<SearchProductDto> searchProductDtos = searchKeywordQueryRepository.selectSearchKeyword(keyword);
        // when

        // then
    }
}