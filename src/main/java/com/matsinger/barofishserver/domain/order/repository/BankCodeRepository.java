package com.matsinger.barofishserver.domain.order.repository;

import com.matsinger.barofishserver.domain.order.domain.BankCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankCodeRepository extends JpaRepository<BankCode, Integer> {
    boolean existsByCode(String code);
}
