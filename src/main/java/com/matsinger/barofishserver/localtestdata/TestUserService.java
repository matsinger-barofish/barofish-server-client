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
import java.util.List;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class TestUserService {

    private final UserRepository userRepository;
    private final UserAuthRepository userAuthRepository;

    public void createUser() {
        for (int i = 1; i < 3; i++) {
            User createdUser = User.builder()
                    .state(UserState.ACTIVE)
                    .joinAt(Timestamp.valueOf(LocalDateTime.now())).build();

            UserAuth createdUserAuth = UserAuth.builder()
                    .loginType(LoginType.IDPW)
                    .loginId("test" + i)
                    .password("test" + i).build();

            createdUserAuth.setUser(createdUser);
            userRepository.save(createdUser);
            userAuthRepository.save(createdUserAuth);
        }
    }
}
