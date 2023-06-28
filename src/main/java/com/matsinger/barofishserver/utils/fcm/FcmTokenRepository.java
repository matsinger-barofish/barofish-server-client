package com.matsinger.barofishserver.utils.fcm;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FcmTokenRepository extends JpaRepository<FcmToken, String> {
    List<FcmToken> findAllByUserId(Integer userId);
}
