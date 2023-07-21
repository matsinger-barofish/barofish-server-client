package com.matsinger.barofishserver.user;

import com.matsinger.barofishserver.grade.repository.GradeRepository;
import com.matsinger.barofishserver.user.application.UserCommandService;
import com.matsinger.barofishserver.user.application.UserQueryService;
import com.matsinger.barofishserver.user.dto.UserJoinReq;
import com.matsinger.barofishserver.user.dto.UserLoginReq;
import com.matsinger.barofishserver.user.domain.User;
import com.matsinger.barofishserver.user.repository.UserRepository;
import com.matsinger.barofishserver.userauth.domain.LoginType;
import com.matsinger.barofishserver.userauth.domain.UserAuth;
import com.matsinger.barofishserver.userauth.application.UserAuthCommandService;
import com.matsinger.barofishserver.userauth.repository.UserAuthRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("local")
@SpringBootTest
class UserQueryServiceTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserAuthRepository userAuthRepository;
    @Autowired
    private GradeRepository gradeRepository;
    @Autowired
    private UserQueryService userQueryService;
    @Autowired
    private UserCommandService userService;
    @Autowired
    private UserAuthCommandService userAuthCommandService;

    @AfterEach
    void tearDown() {
        userAuthRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        gradeRepository.deleteAllInBatch();
    }

    @DisplayName("회원가입을 한 아이디로 로그인을 할 수 있다.")
    @Test
    void test() throws Exception {
        // given
        String email = "test@gmail.com";
        String password = "password1!";
        UserJoinReq joinRequest = createIdPwJoinRequest(email, password);
        User createdUser = userService.createIdPwUserAndSave();
        UserAuth createdUserAuth = userAuthCommandService.createIdPwUserAuthAndSave(createdUser, joinRequest);

        UserLoginReq loginRequest = UserLoginReq.builder()
                .loginType(LoginType.IDPW)
                .loginId(email)
                .password(password)
                .build();

        // when // then
        assertThat(userQueryService.login(loginRequest))
                .isNotNull();
    }

    private UserJoinReq createIdPwJoinRequest(String email, String password) {
        return UserJoinReq.builder()
                .email(email)
                .name("test")
                .password(password)
                .phone("010-1234-1234")
                .verificationId(1234)
                .impUid("test")
                .nickname("test")
                .postalCode("test")
                .address("test")
                .addressDetail("test")
                .isAgreeMarketing(false)
                .build();
    }
}