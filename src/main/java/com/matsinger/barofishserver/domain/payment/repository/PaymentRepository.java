package com.matsinger.barofishserver.domain.payment.repository;

import com.matsinger.barofishserver.domain.payment.domain.Payments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payments, Integer> {
    Payments findFirstByMerchantUid(String id);

    Payments findFirstByImpUid(String impUid);

    List<Payments> findAllByOrderIdIn(List<String> orderIds);
}
