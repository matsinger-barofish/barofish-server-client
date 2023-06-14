package com.matsinger.barofishserver.notification;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    public List<Notification> findAllByUserId(Integer userId);
}
