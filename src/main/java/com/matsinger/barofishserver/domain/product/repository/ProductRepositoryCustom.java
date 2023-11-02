package com.matsinger.barofishserver.domain.product.repository;

import com.matsinger.barofishserver.domain.product.domain.ProductSortBy;
import com.matsinger.barofishserver.domain.product.dto.ProductListDtoV2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepositoryCustom {


    Page<ProductListDtoV2> getProducts(Pageable pageable, ProductSortBy sortBy, String keyword);
}