package com.matsinger.barofishserver.domain.verification;

import com.matsinger.barofishserver.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@RequiredArgsConstructor
@Slf4j
@Service
public class VerificationService {
    private final VerificationRepository verificationRepository;

    public void verifyPhoneVerification(Integer verificationId) {

        Verification verification = null;

        if (verificationId == null) {
            throw new BusinessException("인증을 먼저 진행해주세요.");
        } else if (verificationId != null) {
            verification = selectVerificationById(verificationId);
            if (verification == null || verification.getExpiredAt() != null)
                throw new BusinessException("인증을 먼저 진행해주세요.");
        }
    }

    public Verification selectVerificationById(Integer id) {
        try {
            return verificationRepository.findById(id).orElseThrow(() -> {
                throw new Error("인증 정보를 찾을 수 없습니다.");
            });
        } catch (Error e) {
            return null;
        }
    }

    public Verification selectVerificationByImpUid(String impUid) {
        return verificationRepository.findFirstByTargetEqualsOrderByCreateAtDesc(impUid);
    }

    public String generateVerificationCode(int length) {
        StringBuilder code = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int digit = random.nextInt(10);
            code.append(digit);
        }

        return code.toString();
    }

    public Verification selectVerification(String target, String verificationNumber) {
        try {
            return verificationRepository.findFirstByTargetEqualsAndVerificationNumberEqualsOrderByIdDesc(target,
                    verificationNumber);
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
