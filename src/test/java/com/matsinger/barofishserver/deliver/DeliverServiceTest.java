//package com.matsinger.barofishserver.deliver;
//
//import com.matsinger.barofishserver.deliver.application.DeliverService;
//import com.matsinger.barofishserver.grade.domain.Grade;
//import com.matsinger.barofishserver.grade.repository.GradeRepository;
//import com.matsinger.barofishserver.user.deliverplace.repository.DeliverPlaceRepository;
//import com.matsinger.barofishserver.user.repository.UserRepository;
//import com.matsinger.barofishserver.user.application.UserCommandService;
//import com.matsinger.barofishserver.user.dto.SnsJoinReq;
//import com.matsinger.barofishserver.user.dto.UserJoinReq;
//import com.matsinger.barofishserver.user.deliverplace.DeliverPlace;
//import com.matsinger.barofishserver.user.domain.User;
//import com.matsinger.barofishserver.userauth.domain.UserAuth;
//import com.matsinger.barofishserver.userauth.repository.UserAuthRepository;
//import com.matsinger.barofishserver.userinfo.domain.UserInfo;
//import com.matsinger.barofishserver.userinfo.repository.UserInfoRepository;
//import com.matsinger.barofishserver.userinfo.application.UserInfoCommandService;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//
//import static com.matsinger.barofishserver.userauth.domain.LoginType.IDPW;
//import static org.assertj.core.api.Assertions.*;
//
//@ActiveProfiles("local")
//@SpringBootTest
//class DeliverServiceTest {
//
//        @Autowired
//        private UserRepository userRepository;
//        @Autowired
//        private UserAuthRepository userAuthRepository;
//        @Autowired
//        private UserInfoRepository userInfoRepository;
//        @Autowired
//        private GradeRepository gradeRepository;
//        @Autowired
//        private DeliverPlaceRepository deliverPlaceRepository;
//
//        @Autowired
//        private UserCommandService userService;
//        @Autowired
//        private UserInfoCommandService userInfoCommandService;
//        @Autowired
//        private DeliverService deliveryService;
//
//        @AfterEach
//        void tearDown() {
//                deliverPlaceRepository.deleteAllInBatch();
//                userInfoRepository.deleteAllInBatch();
//                userAuthRepository.deleteAllInBatch();
//                userRepository.deleteAllInBatch();
//                gradeRepository.deleteAllInBatch();
//        }
//
//        @DisplayName("sns가 아닌 회원 정보를 입력해 회원가입을 할 경우 배송 정보를 입력 받아 저장할 수 있다.")
//        @Test
//        void test() throws Exception {
//                // given
//                UserJoinReq request = createIdPwJoinRequest();
//                Grade createdGrade = createAndSaveGrade();
//
//                User createdUser = userService.createIdPwUserAndSave();
//                UserInfo createdUserInfo = userInfoCommandService.createAndSaveIdPwUserInfo(createdUser, request,
//                                createdGrade);
//                DeliverPlace createdDeliver = deliveryService.createAndSaveDeliverPlace(createdUser, createdUserInfo,
//                                request);
//
//                // when // then
//                assertThat(deliverPlaceRepository.findById(createdDeliver.getId()))
//                                .isPresent();
//        }
//
//        private Grade createAndSaveGrade() {
//                Grade grade = Grade.builder()
//                                .name("grade")
//                                .pointRate(1)
//                                .minOrderPrice(1000)
//                                .minOrderCount(10)
//                                .build();
//                return gradeRepository.save(grade);
//        }
//
//        private UserAuth createAndSaveUserAuth(User createdUser) {
//                UserAuth userAuth = UserAuth.builder()
//                                .loginType(IDPW)
//                                .loginId("test")
//                                .userId(createdUser.getId())
//                                .password("test")
//                                .build();
//                userAuth.setUser(createdUser);
//                return userAuthRepository.save(userAuth);
//        }
//
//        private UserJoinReq createIdPwJoinRequest() {
//                return UserJoinReq.builder()
//                                .email("test@gmail.com")
//                                .name("test")
//                                .password("password1!")
//                                .phone("010-1234-1234")
//                                .verificationId(1234)
//                                .impUid("test")
//                                .nickname("test")
//                                .postalCode("test")
//                                .address("test")
//                                .addressDetail("test")
//                                .isAgreeMarketing(false)
//                                .build();
//        }
//
//        private SnsJoinReq createJoinRequest(String loginId) {
//                return SnsJoinReq.builder()
//                                .loginType(IDPW)
//                                .loginId(loginId)
//                                .profileImage(null)
//                                .email("test@gmail.com")
//                                .name("test")
//                                .nickname("test")
//                                .phone("010-1234-1234").build();
//        }
//}