package com.matsinger.barofishserver.domain.verification;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationRepository extends JpaRepository<Verification, Integer> {
    Verification findFirstByTargetEqualsAndVerificationNumberEqualsOrderByIdDesc(String target,
                                                                                 String verificationNumber);

    Verification findFirstByTargetEqualsOrderByCreateAtDesc(String target);
}
