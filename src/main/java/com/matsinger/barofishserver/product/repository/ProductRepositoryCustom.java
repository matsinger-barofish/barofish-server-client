package com.matsinger.barofishserver.product.repository;

import com.matsinger.barofishserver.product.domain.ProductSortBy;
import com.matsinger.barofishserver.product.dto.ProductListDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepositoryCustom {


    Page<ProductListDto> getProducts(Pageable pageable, ProductSortBy sortBy, int userId);
}
