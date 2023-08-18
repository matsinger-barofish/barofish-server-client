package com.matsinger.barofishserver.userinfo.application;

import com.matsinger.barofishserver.compare.application.CompareItemQueryService;
import com.matsinger.barofishserver.notification.repository.NotificationRepository;
import com.matsinger.barofishserver.review.repository.ReviewRepository;
import com.matsinger.barofishserver.user.deliverplace.repository.DeliverPlaceRepository;
import com.matsinger.barofishserver.user.deliverplace.DeliverPlace;
import com.matsinger.barofishserver.userinfo.domain.UserInfo;
import com.matsinger.barofishserver.userinfo.dto.UserInfoDto;
import com.matsinger.barofishserver.userinfo.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserInfoQueryService {

    private final UserInfoRepository userInfoRepository;
    private final DeliverPlaceRepository deliverPlaceRepository;
    private final ReviewRepository reviewRepository;
    private final NotificationRepository notificationRepository;
    private final CompareItemQueryService compareItemQueryService;

    public UserInfoDto showMyPage(Integer userId) {

        UserInfo findUserInfo = userInfoRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    throw new IllegalArgumentException("유저 정보를 찾을 수 없습니다.");
                });

        List<DeliverPlace> deliverPlaces = deliverPlaceRepository.findAllByUserId(userId);
        Integer reviewCount = reviewRepository.countAllByUserId(userId);
        Integer notificationCount = notificationRepository.countAllByUserId(userId);
        Integer saveProductCount = compareItemQueryService.countSaveProductWithUserId(userId);
        return UserInfoDto.builder()
                .userId(userId)
                .profileImage(findUserInfo.getProfileImage())
                .grade(findUserInfo.getGrade())
                .email(findUserInfo.getEmail())
                .name(findUserInfo.getName())
                .nickname(findUserInfo.getNickname())
                .phone(findUserInfo.getPhone())
                .isAgreeMarketing(findUserInfo.getIsAgreeMarketing())
                .reviewCount(reviewCount)
                .notificationCount(notificationCount)
                .deliverPlaces(deliverPlaces)
                .point(findUserInfo.getPoint())
                .saveProductCount(saveProductCount)
                .build();
    }
}
