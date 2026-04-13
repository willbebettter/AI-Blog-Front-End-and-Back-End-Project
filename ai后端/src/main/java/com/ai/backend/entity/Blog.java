package com.ai.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "blog")
public class Blog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "cover_image", columnDefinition = "TEXT")
    private String coverImage;

    @Column(length = 500)
    private String summary;

    @Column(length = 50)
    private String category;

    @Column(length = 500)
    private String tags;

    @Column(name = "author_id")
    private Long authorId;

    @Column(nullable = false)
    private Integer views;

    @Column(nullable = false)
    private Integer likes;

    @Column(nullable = false)
    private Integer collects;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @PrePersist
    public void prePersist() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
        if (this.views == null) this.views = 0;
        if (this.likes == null) this.likes = 0;
        if (this.collects == null) this.collects = 0;
        if (this.status == null) this.status = Status.DRAFT;
    }

    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }

    public enum Status {
        PUBLISHED,
        DRAFT,
        DELETED
    }
}
