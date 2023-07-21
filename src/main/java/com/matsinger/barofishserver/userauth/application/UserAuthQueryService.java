package com.matsinger.barofishserver.userauth.application;

import com.matsinger.barofishserver.user.dto.UserLoginReq;
import com.matsinger.barofishserver.userauth.domain.UserAuth;
import com.matsinger.barofishserver.userauth.repository.UserAuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserAuthQueryService {

    private final UserAuthRepository userAuthRepository;

    public UserAuth login(UserLoginReq request) {
        UserAuth findUserAuth = userAuthRepository.findByLoginTypeAndLoginId(request.getLoginType(), request.getLoginId())
                .orElseThrow(() -> {
                    throw new IllegalStateException("아이디 및 비밀번호를 확인해주세요.");
                });
        if (!BCrypt.checkpw(request.getPassword(), findUserAuth.getPassword())) {
            throw new IllegalArgumentException("아이디 및 비밀번호를 확인해주세요.");
        }
        return findUserAuth;
    }
}
