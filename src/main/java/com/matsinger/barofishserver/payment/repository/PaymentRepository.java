package com.matsinger.barofishserver.payment.repository;

import com.matsinger.barofishserver.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
