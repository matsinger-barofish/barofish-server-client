package com.matsinger.barofishserver.userauth;

import com.matsinger.barofishserver.grade.domain.Grade;
import com.matsinger.barofishserver.grade.repository.GradeRepository;
import com.matsinger.barofishserver.user.deliverplace.repository.DeliverPlaceRepository;
import com.matsinger.barofishserver.user.repository.UserRepository;
import com.matsinger.barofishserver.user.application.UserCommandService;
import com.matsinger.barofishserver.user.dto.SnsJoinReq;
import com.matsinger.barofishserver.user.dto.UserJoinReq;
import com.matsinger.barofishserver.user.dto.UserUpdateReq;
import com.matsinger.barofishserver.user.domain.User;
import com.matsinger.barofishserver.user.domain.UserState;
import com.matsinger.barofishserver.userauth.domain.UserAuth;
import com.matsinger.barofishserver.userauth.repository.UserAuthRepository;
import com.matsinger.barofishserver.userauth.application.UserAuthCommandService;
import com.matsinger.barofishserver.userinfo.domain.UserInfo;
import com.matsinger.barofishserver.userinfo.application.UserInfoCommandService;
import com.matsinger.barofishserver.userinfo.repository.UserInfoRepository;
import com.matsinger.barofishserver.utils.Common;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.context.ActiveProfiles;

import static com.matsinger.barofishserver.userauth.domain.LoginType.IDPW;
import static com.matsinger.barofishserver.userauth.domain.LoginType.KAKAO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("local")
@SpringBootTest
class UserAuthCommandServiceTest {

        @Autowired
        GradeRepository gradeRepository;
        @Autowired
        private UserAuthRepository userAuthRepository;
        @Autowired
        private UserRepository userRepository;
        @Autowired
        private UserInfoRepository userInfoRepository;
        @Autowired
        private UserCommandService userService;
        @Autowired
        private UserAuthCommandService userAuthCommandService;
        @Autowired
        private UserInfoCommandService userInfoCommandService;

        @Autowired
        private EntityManager em;

        @Autowired
        private Common utils;
        @Autowired
        private DeliverPlaceRepository deliverPlaceRepository;

        @AfterEach
        void tearDown() {
                userAuthRepository.deleteAllInBatch();
                deliverPlaceRepository.deleteAllInBatch();
                userInfoRepository.deleteAllInBatch();
                userRepository.deleteAllInBatch();
                gradeRepository.deleteAllInBatch();
        }

        @DisplayName("회원의 상태가 ACTIVE이지 않은 경우 UserAuth를 저장할 때 각 상황에 맞는 에러를 던진다.")
        @Test
        void throwErrorWhenUserStatusIsNotActive() {
                // given
                String loginId = "test";
                SnsJoinReq request = createJoinRequest(loginId);
                User createdUser = userService.createSnsUserAndSave(request);

                UserAuth userAuth = UserAuth.builder()
                                .loginType(KAKAO)
                                .loginId(loginId)
                                .userId(createdUser.getId())
                                .password("test")
                                .build();
                userAuth.setUser(createdUser);
                userAuthRepository.save(userAuth);

                // when
                createdUser.setState(UserState.BANNED);
                User savedUser = userRepository.save(createdUser);

                // then
                assertThatThrownBy(() -> userAuthCommandService.createUserAuth(request, savedUser))
                                .isInstanceOf(IllegalArgumentException.class)
                                .hasMessageContaining("정지된 유저입니다.");
        }

        @DisplayName("Sns가 아닌 ID, Password 등의 개인정보를 입력해 회원가입을 할 수 있다.")
        @Test
        void createIdPwUserAndSave() throws Exception {
                // given
                UserJoinReq request = createIdPwJoinRequest();
                Grade createdGrade = createAndSaveGrade();

                User createdUser = userService.createIdPwUserAndSave();
                UserAuth createdUserAuth = userAuthCommandService.createIdPwUserAuthAndSave(createdUser, request);
                UserInfo userInfo = userInfoCommandService.createAndSaveIdPwUserInfo(createdUser, request,
                                createdGrade);

                // when // then
                assertThat(userAuthRepository.findByLoginId(createdUserAuth.getLoginId()))
                                .isPresent();
                assertThat(userRepository.findById(createdUserAuth.getUserId()))
                                .isPresent();
                assertThat(userInfoRepository.findByUserId(createdUser.getId()))
                                .isPresent();
        }

        @DisplayName("유저는 비밀번호를 업데이트할 수 있다." +
                        "디버깅해서 oldPassword, newPassword를" +
                        "비교해 보면 정상 업데이트 되는 것을 확인 가능")
        @Test
        void testForUpdatePassword() throws Exception {
                // given
                UserJoinReq joinRequest = createIdPwJoinRequest();
                Grade createdGrade = createAndSaveGrade();

                User createdUser = userService.createIdPwUserAndSave();
                UserAuth savedUserAuth = userAuthCommandService.createIdPwUserAuthAndSave(createdUser, joinRequest);

                // when
                UserUpdateReq updateRequest = UserUpdateReq.builder()
                                .name("test")
                                .nickname("test")
                                .oldPassword("password1!")
                                .newPassword("password2@")
                                .phone("010-1234-1234")
                                .verificationId(1)
                                .address("test")
                                .addressDetail("test")
                                .isAgreeMarketing(false)
                                .build();

                userAuthCommandService.updateUserAuth(createdUser.getId(), updateRequest);
                UserAuth updatedUserAuth = userAuthRepository.save(savedUserAuth);
                String newPassword = updatedUserAuth.getPassword();

                // then
        }

        private UserUpdateReq createUpdateReq() {
                return UserUpdateReq.builder()
                                .name("test")
                                .nickname("test")
                                .oldPassword("password1!")
                                .newPassword("password2@")
                                .phone("010-1234-1234")
                                .verificationId(1)
                                .address("test")
                                .addressDetail("test")
                                .isAgreeMarketing(false)
                                .build();
        }

        private Grade createAndSaveGrade() {
                Grade grade = Grade.builder()
                                .name("grade")
                                .pointRate(1)
                                .minOrderPrice(1000)
                                .minOrderCount(10)
                                .build();
                return gradeRepository.save(grade);
        }

        private UserAuth createAndSaveUserAuth(User createdUser) {
                UserAuth userAuth = UserAuth.builder()
                                .loginType(IDPW)
                                .loginId("test")
                                .userId(createdUser.getId())
                                .password("test")
                                .build();
                userAuth.setUser(createdUser);
                return userAuthRepository.save(userAuth);
        }

        private UserJoinReq createIdPwJoinRequest() {
                return UserJoinReq.builder()
                                .email("test@gmail.com")
                                .name("test")
                                .password("password1!")
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