package com.matsinger.barofishserver.order.repository;

import com.matsinger.barofishserver.order.domain.BankCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankCodeRepository extends JpaRepository<BankCode, Integer> {
    boolean existsByCode(String code);
}
