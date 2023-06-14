package com.matsinger.barofishserver.verification;

import com.matsinger.barofishserver.utils.CustomResponse;
import com.matsinger.barofishserver.utils.RegexConstructor;
import lombok.*;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/verification")
public class VerificationController {

    private final VerificationService verificationService;

    private final RegexConstructor re;

    @Getter
    @ToString
    @NoArgsConstructor
    static class RequestCodeReq {
        private String target;
    }

    @PostMapping("/code")
    public ResponseEntity<CustomResponse<Boolean>> requestCodeVerification(@RequestBody RequestCodeReq data) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        try {
            String phone = data.target.replaceAll(re.getPhone(), "0$1$2$3");
            verificationService.addVerification(Verification.builder().verificationNumber("123456").expiredAt(new Timestamp(
                    System.currentTimeMillis() +
                            TimeUnit.MINUTES.toMillis(3))).createAt(new Timestamp(System.currentTimeMillis())).target(
                    phone).build());
            return res.throwError("인증번호는 [123456] 입니다.\nToast API 정보가 없어서 실제 SMS 발송을 진행할 수 없습니다.", "INTERNAL_ERROR");
//            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @Getter
    @ToString
    @NoArgsConstructor
    static class VerifyCodeReq {
        private String target;
        private String verificationNumber;
    }

    @PostMapping("/verify")
    public ResponseEntity<CustomResponse<Integer>> verifyCode(@RequestBody VerifyCodeReq data) {
        CustomResponse<Integer> res = new CustomResponse<>();
        try {
            String phone = data.target.replaceAll(re.getPhone(), "0$1$2$3");
            Verification verification = verificationService.selectVerification(phone, data.getVerificationNumber());
            if (verification == null) return res.throwError("인증 정보가 없거나 만료되었습니다. 다시 시도해주세요.", "INTERNAL_SERVER_ERROR");
            if (verification.getExpiredAt() != null &&
                    verification.getExpiredAt().before(new Timestamp(System.currentTimeMillis()))) {
                verificationService.deleteVerification(verification.getId());
                return res.throwError("인증정보가 없거나 만료되었습니다. 다시 시도해주세요.", "INTERNAL_SERVER_ERROR");
            }
            verification.setExpiredAt(null);
            verificationService.updateVerification(verification);
            res.setData(Optional.of(verification.getId()));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }
}
