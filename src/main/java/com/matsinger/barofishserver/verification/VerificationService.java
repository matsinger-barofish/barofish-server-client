package com.matsinger.barofishserver.verification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class VerificationService {
    private final VerificationRepository verificationRepository;

    public Verification selectVerificationById(Integer id) {
        try {
            return verificationRepository.findById(id).orElseThrow(() -> {
                throw new Error("인증 정보를 찾을 수 없습니다.");
            });
        } catch (Error e) {
            return null;
        }
    }

    public Verification selectVerification(String target, String verificationNumber) {
        try {
            System.out.println(target + verificationNumber);
            return verificationRepository.findFirstByTargetEqualsAndVerificationNumberEqualsOrderByIdDesc(target, verificationNumber);
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public Verification addVerification(Verification verification) {
        return verificationRepository.save(verification);
    }

    public Boolean deleteVerification(Integer id) {
        try {
            verificationRepository.deleteById(id);
            return true;
        } catch (Error e) {
            return false;
        }
    }

    public Verification updateVerification(Verification verification) {
        return verificationRepository.save(verification);
    }
}
