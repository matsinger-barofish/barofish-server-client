package com.matsinger.barofishserver.user.exception;

import com.matsinger.barofishserver.user.domain.User;
import com.matsinger.barofishserver.user.domain.UserState;
import com.matsinger.barofishserver.userauth.domain.LoginType;
import com.matsinger.barofishserver.utils.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class UserException {

    private final CustomResponse customResponse;

    public void validateUserState(User findUser) {
        if (findUser.getState().equals(UserState.BANNED)) {
            throw new IllegalArgumentException("정지된 유저입니다.");
        }
        if (findUser.getState().equals(UserState.DELETED)) {
            throw new IllegalArgumentException("삭제된 유저입니다.");
        }
        if (!findUser.getState().equals(UserState.ACTIVE)) {
            throw new IllegalArgumentException("활성화된 유저가 아닙니다.");
        }
    }

    public void nullCheck(LoginType loginType) {
        if (loginType == null) {
            customResponse.throwError("로그인 타입을 입력해주세요.", "INPUT_CHECK_REQUIRED");
        }
    }
    public void nullCheck(String loginId) {
        if (loginId == null) {
            customResponse.throwError("로그인 아이디를 입력해주세요.", "INPUT_CHECK_REQUIRED");
        }
    }

    public void checkUserState(User user) {
        if (user.getState().equals(UserState.BANNED)) {
            customResponse.throwError("정지된 사용자입니다.", "NOT_ALLOWED");
        }
        if (user.getState().equals(UserState.DELETED)) {
            customResponse.throwError("삭제된 사용자입니다.", "NOT_ALLOWED");
        }
    }
}
