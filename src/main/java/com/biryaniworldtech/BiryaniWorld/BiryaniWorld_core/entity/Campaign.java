package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "campaigns")
public class Campaign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private LocalDateTime scheduledTime;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private String targetAudience; // ALL, ACTIVE_USERS, NEW_USERS, etc.

    @Column
    private LocalDateTime sentAt;

    @Column
    private int totalRecipients;

    @Column
    private int openedCount;

    @Column
    private int clickedCount;

    @Column
    private int bouncedCount;

    @PrePersist
    protected void onCreate() {
        if (scheduledTime == null) {
            scheduledTime = LocalDateTime.now();
        }
    }
} 