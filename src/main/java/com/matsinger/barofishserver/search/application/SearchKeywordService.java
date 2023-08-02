package com.matsinger.barofishserver.search.application;

import com.matsinger.barofishserver.product.repository.ProductRepository;
import com.matsinger.barofishserver.product.domain.Product;
import com.matsinger.barofishserver.product.dto.ProductListDto;
import com.matsinger.barofishserver.search.domain.SearchKeyword;
import com.matsinger.barofishserver.search.repository.SearchKeywordRepository;
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






}
