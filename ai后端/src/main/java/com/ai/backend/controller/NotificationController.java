package com.ai.backend.controller;

import com.ai.backend.common.Result;
import com.ai.backend.entity.Notification;
import com.ai.backend.mapper.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    @GetMapping("/{userId}")
    public Result<List<Notification>> getNotifications(@PathVariable Long userId) {
        return Result.success(notificationRepository.findByUserIdOrderByCreateTimeDesc(userId));
    }

    @GetMapping("/{userId}/unread-count")
    public Result<Long> getUnreadCount(@PathVariable Long userId) {
        return Result.success(notificationRepository.countByUserIdAndIsReadFalse(userId));
    }

    @PutMapping("/{id}/read")
    public Result<Object> markAsRead(@PathVariable Long id) {
        return notificationRepository.findById(id)
                .map(notification -> {
                    notification.setIsRead(true);
                    notificationRepository.save(notification);
                    return Result.success(null);
                })
                .orElse(Result.error(404, "通知不存在"));
    }

    @PutMapping("/{userId}/read-all")
    @Transactional
    public Result<Object> markAllAsRead(@PathVariable Long userId) {
        notificationRepository.markAllAsReadByUserId(userId);
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    public Result<Object> deleteNotification(@PathVariable Long id) {
        if (notificationRepository.existsById(id)) {
            notificationRepository.deleteById(id);
            return Result.success(null);
        }
        return Result.error(404, "通知不存在");
    }
}
