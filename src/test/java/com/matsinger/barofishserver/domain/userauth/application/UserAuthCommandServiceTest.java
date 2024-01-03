package com.matsinger.barofishserver.domain.userauth.application;

import com.matsinger.barofishserver.domain.user.application.UserCommandService;
import com.matsinger.barofishserver.domain.user.dto.UserJoinReq;
import com.matsinger.barofishserver.utils.RegexConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("local")
class UserAuthCommandServiceTest {

    @Autowired private RegexConstructor re;
    @Autowired private UserCommandService userCommandService;

    @DisplayName("")
    @Test
    void test() {
        // given
        userCommandService.createIdPwUserAndSave(
                UserJoinReq.builder()
                        .email("test1@gmail.com")
                        .name("test")
                        .nickname("test8")
                        .password("Aa!!123123123")
                        .phone("01000000008")
                        .verificationId(null)
                        .impUid(null)
                        .bcode("123")
                        .postalCode("123")
                        .address("test")
                        .addressDetail("test")
                        .isAgreeMarketing(false)
                        .build()
        );
        // when

        // then
    }
}