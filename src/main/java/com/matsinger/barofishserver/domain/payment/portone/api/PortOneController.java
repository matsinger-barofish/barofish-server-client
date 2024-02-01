package com.matsinger.barofishserver.domain.payment.portone.api;

import com.matsinger.barofishserver.domain.payment.portone.application.PortOneQueryService;
import com.matsinger.barofishserver.domain.payment.portone.dto.AccountCheckRequest;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.utils.CustomResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/portone")
public class PortOneController {

    private final JwtService jwt;
    private final PortOneQueryService portOneQueryService;

    // https://developers.portone.io/api/rest-v1/vbank 참고
    @PostMapping("/check-account")
    public ResponseEntity<CustomResponse<Boolean>> checkAccount(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                @RequestBody AccountCheckRequest request) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);

        String holderName = request.getHolderName().trim();
        String bankNum = request.getBankNum().trim();
        String errorMessage = portOneQueryService.checkVbankAccountVerification(request.getBankCodeId(), bankNum, holderName);
        boolean isSuccess = false;
        if (errorMessage == null) {
            isSuccess = true;
        }
        if (errorMessage != null) {
            isSuccess = false;
        }

        res.setIsSuccess(true);
        res.setData(Optional.ofNullable(isSuccess));
        res.setErrorMsg(errorMessage);
        return ResponseEntity.ok(res);
    }
}
