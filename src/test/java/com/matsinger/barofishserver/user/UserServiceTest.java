package com.matsinger.barofishserver.user;

import com.matsinger.barofishserver.domain.user.application.UserCommandService;
import com.matsinger.barofishserver.domain.user.domain.UserState;
import com.matsinger.barofishserver.domain.user.dto.SnsJoinReq;
import com.matsinger.barofishserver.domain.user.domain.User;
import com.matsinger.barofishserver.domain.user.repository.UserRepository;
import com.matsinger.barofishserver.domain.userauth.domain.UserAuth;
import com.matsinger.barofishserver.domain.userauth.repository.UserAuthRepository;
import com.matsinger.barofishserver.domain.userauth.application.UserAuthCommandService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Timestamp;

import static com.matsinger.barofishserver.domain.userauth.domain.LoginType.*;
import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("local")
@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAuthRepository userAuthRepository;

    @Autowired
    private UserCommandService userService;
    @Autowired
    private UserAuthCommandService userAuthCommandService;

    @AfterEach
    void tearDown() {
        userAuthRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("회원가입시 중복된 아이디가 있으면 회원가입을 할 수 없다.")
    @Test
    void createUserWithDuplicatedLoginId() {
        // given
        String loginId = "test";
        SnsJoinReq request = createJoinRequest(loginId);
        User createdUser = createSnsUserAndSave(request);

        UserAuth userAuth = UserAuth.builder()
                .loginType(IDPW)
                .loginId(loginId)
                .userId(createdUser.getId())
                .password("test")
                .build();
        userAuth.setUser(createdUser);
        userAuthRepository.save(userAuth);

        // when // then
        assertThatThrownBy(() -> userService.createSnsUserAndSave(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("중복된 아이디가 존재합니다.");
    }

    private User createSnsUserAndSave(SnsJoinReq request) {
        User user = User.builder()
                .joinAt(new Timestamp(System.currentTimeMillis()))
                .state(UserState.ACTIVE)
                .build();
        return userRepository.save(user);
    }

    private SnsJoinReq createJoinRequest(String loginId) {
        return SnsJoinReq.builder()
                .loginType(IDPW)
                .loginId(loginId)
                .profileImage(null)
                .email("test@gmail.com")
                .name("test")
                .nickname("test")
                .phone("010-1234-1234").build();
    }
}