package com.ai.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "from_user_id")
    private Long fromUserId;

    @Column(length = 50)
    private String type;

    @Column(length = 200)
    private String title;

    @Column(length = 500)
    private String content;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    @PrePersist
    public void prePersist() {
        this.createTime = LocalDateTime.now();
        if (this.isRead == null) this.isRead = false;
    }
}
