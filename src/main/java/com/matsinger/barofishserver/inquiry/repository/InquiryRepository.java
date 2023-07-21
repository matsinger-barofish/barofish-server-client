package com.matsinger.barofishserver.inquiry.repository;

import com.matsinger.barofishserver.inquiry.domain.Inquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface InquiryRepository extends JpaRepository<Inquiry, Integer>, JpaSpecificationExecutor<Inquiry> {
    List<Inquiry> findAllByProductId(Integer productId);

    Page<Inquiry> findAllByProduct_StoreId(Integer storeId, Pageable pageable);

    void deleteAllByUserId(Integer userId);

    void deleteAllByIdIn(List<Integer> ids);
}
