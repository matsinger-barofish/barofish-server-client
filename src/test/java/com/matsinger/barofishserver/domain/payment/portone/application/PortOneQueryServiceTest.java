package com.matsinger.barofishserver.domain.payment.portone.application;

import com.matsinger.barofishserver.domain.payment.portone.dto.PortOneVbankHolderResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("local")
class PortOneQueryServiceTest {

    @Autowired private PortOneQueryService portOneQueryService;

    @DisplayName("")
    @Test
    void test() {
        // given
      portOneQueryService.generateAccessToken();
      ResponseEntity<PortOneVbankHolderResponse> responseEntity = portOneQueryService.checkVbankAccountVerification("004", "6727020142211");
      HttpStatusCode statusCode = responseEntity.getStatusCode();
      PortOneVbankHolderResponse body = responseEntity.getBody();
        // when

        // then
    }
}