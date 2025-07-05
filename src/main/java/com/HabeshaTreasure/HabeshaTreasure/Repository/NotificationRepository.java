package com.HabeshaTreasure.HabeshaTreasure.Repository;

import com.HabeshaTreasure.HabeshaTreasure.Entity.Notification;
import com.HabeshaTreasure.HabeshaTreasure.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByOrderByCreatedAtDesc();
    List<Notification> findByUserOrderByCreatedAtDesc(User user);
    List<Notification> findByIsReadFalseAndUser(User user);
    List<Notification> findByUserIsNullOrderByCreatedAtDesc(); // For global/admin notifications
    void deleteByUser(User user);

}
