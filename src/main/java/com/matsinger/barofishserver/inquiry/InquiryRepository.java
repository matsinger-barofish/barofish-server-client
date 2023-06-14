package com.matsinger.barofishserver.inquiry;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InquiryRepository extends JpaRepository<Inquiry, Integer> {
    List<Inquiry> findAllByProductId(Integer productId);

    List<Inquiry> findAllByProduct_StoreId(Integer storeId);

    void deleteAllByUserId(Integer userId);
}
