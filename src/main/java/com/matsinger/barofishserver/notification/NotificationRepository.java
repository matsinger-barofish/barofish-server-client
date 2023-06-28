package com.matsinger.barofishserver.notification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    public Page<Notification> findAllByUserId(Integer userId, PageRequest pageRequest);

    Integer countAllByUserId(Integer userId);
}
