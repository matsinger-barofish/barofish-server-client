package com.matsinger.barofishserver.payment;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository  extends JpaRepository<Payments, Integer> {
    Payments findFirstByMerchantUid(String id);

    Payments findFirstByImpUid(String impUid);
}
