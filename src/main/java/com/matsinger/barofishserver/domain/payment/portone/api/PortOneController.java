package com.matsinger.barofishserver.domain.payment.portone.api;

import com.matsinger.barofishserver.domain.payment.portone.application.PortOneQueryService;
import com.matsinger.barofishserver.domain.payment.portone.dto.AccountCheckRequest;
import com.matsinger.barofishserver.domain.payment.portone.dto.PortOneVbankHolderResponse;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.utils.CustomResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Optional;
import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/portone")
public class PortOneController {

    private final JwtService jwt;
    private final PortOneQueryService portOneQueryService;

    @PostMapping("/check-account")
    public ResponseEntity<CustomResponse<Boolean>> checkAccount(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                @RequestBody AccountCheckRequest request) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);

        boolean isAuthorized = false;
        try {
            ResponseEntity<PortOneVbankHolderResponse> responseEntity = portOneQueryService.checkVbankAccountVerification(request.getBankCodeId(), request.getBankNum());
            if (responseEntity == null) {
                isAuthorized = false;
            }
            if (responseEntity != null) {
                String bankHolder = responseEntity.getBody().getResponse().getBank_holder();
                if (bankHolder.equals(request.getHolderName())) {
                    isAuthorized = true;
                }
            }
        } catch (HttpClientErrorException e) {
            isAuthorized = false;
        }

        res.setIsSuccess(true);
        res.setData(Optional.ofNullable(isAuthorized));
        return ResponseEntity.ok(res);
    }
}
