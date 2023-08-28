package com.matsinger.barofishserver.notification.repository;

import com.matsinger.barofishserver.notification.domain.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    Page<Notification> findAllByUserId(Integer userId, PageRequest pageRequest);

    Integer countAllByUserId(Integer userId);

    void deleteAllByUserIdIn(List<Integer> userIds);
}
