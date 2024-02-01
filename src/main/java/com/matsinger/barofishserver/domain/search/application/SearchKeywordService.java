package com.matsinger.barofishserver.domain.search.application;

import com.matsinger.barofishserver.domain.product.repository.ProductRepository;
import com.matsinger.barofishserver.domain.search.repository.SearchKeywordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class SearchKeywordService {
    private final SearchKeywordRepository searchKeywordRepository;
    private final ProductRepository productRepository;






}
