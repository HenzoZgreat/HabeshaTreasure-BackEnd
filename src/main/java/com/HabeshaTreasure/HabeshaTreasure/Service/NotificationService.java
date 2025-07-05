package com.HabeshaTreasure.HabeshaTreasure.Service;


import com.HabeshaTreasure.HabeshaTreasure.Entity.Notification;
import com.HabeshaTreasure.HabeshaTreasure.Entity.NotificationType;
import com.HabeshaTreasure.HabeshaTreasure.Entity.User;
import com.HabeshaTreasure.HabeshaTreasure.Repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    @Autowired
    private final NotificationRepository notificationRepository;


    public List<Notification> getAllNotifications() {
        return notificationRepository.findAllByOrderByCreatedAtDesc();
    }


    public Notification createNotification(String message, NotificationType type, User user) {
        Notification notification = Notification.builder()
                .message(message)
                .type(type)
                .user(user) // null if global
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .build();
        return notificationRepository.save(notification);
    }

    public List<Notification> getUserNotifications(User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public List<Notification> getUnreadNotifications(User user) {
        return notificationRepository.findByIsReadFalseAndUser(user);
    }

    public List<Notification> getGlobalNotifications() {
        return notificationRepository.findByUserIsNullOrderByCreatedAtDesc();
    }

    public void markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id).orElseThrow();
        notification.setRead(true);
        notificationRepository.save(notification);
    }
}
