package com.matsinger.barofishserver.localtestdata;

import com.matsinger.barofishserver.user.User;
import com.matsinger.barofishserver.user.UserRepository;
import com.matsinger.barofishserver.user.UserState;
import com.matsinger.barofishserver.userauth.LoginType;
import com.matsinger.barofishserver.userauth.UserAuth;
import com.matsinger.barofishserver.userauth.UserAuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 결제 시나리오
 * 유저A
 *  - 주문A: 카드
 *  - 주문B: 무통장
 *
 * 유저B
 *  - 주문A: 결제 fail
 *  - 주문B: 결제 취소
 *
 * 유저C
 *  - 주문A: 결제 테이블 생성x
 */
@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class TestUserService {

    private final UserRepository userRepository;
    private final UserAuthRepository userAuthRepository;
    public static final List<String> suffixes = List.of("A", "B", "C", "D");

    /**
     * return: List<UserAuth> -> 추후에 수정 가능성 있음.
     */
    public UserAuth createUser(String suffix) {
        User createdUser = User.builder()
                .state(UserState.ACTIVE)
                .joinAt(Timestamp.valueOf(LocalDateTime.now())).build();

        UserAuth createdUserAuth = UserAuth.builder()
                .loginType(LoginType.IDPW)
                .loginId("user" + suffix)
                .password("user" + suffix).build();

        createdUserAuth.setUser(createdUser);
        userRepository.save(createdUser);
        UserAuth savedAuth = userAuthRepository.save(createdUserAuth);
        return savedAuth;
    }
}
