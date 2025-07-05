package com.HabeshaTreasure.HabeshaTreasure.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Message content
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    // Type of notification (INFO, ORDER, PRODUCT, USER, SYSTEM)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationType type;

    // Optional target user (null for global/admin system messages)
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Timestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // Read status
    @Column(nullable = false)
    private boolean isRead = false;
}

