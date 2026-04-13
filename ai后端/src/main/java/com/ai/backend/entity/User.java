package com.ai.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(length = 100)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String avatar;

    @Column(length = 50)
    private String nickname;

    @Column(length = 500)
    private String bio;

    @Column(nullable = false)
    private String role;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    @Column(name = "last_login_time", nullable = false)
    private LocalDateTime lastLoginTime;

    @PrePersist
    public void prePersist() {
        this.createTime = LocalDateTime.now();
        this.lastLoginTime = LocalDateTime.now();
        if (this.role == null) this.role = "USER";
    }

    @PreUpdate
    public void preUpdate() {
        this.lastLoginTime = LocalDateTime.now();
    }
}
