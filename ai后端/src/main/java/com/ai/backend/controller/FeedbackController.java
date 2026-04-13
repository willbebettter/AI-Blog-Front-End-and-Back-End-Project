package com.ai.backend.controller;

import com.ai.backend.common.Result;
import com.ai.backend.entity.Feedback;
import com.ai.backend.mapper.FeedbackRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @PostMapping
    public Result<Object> submitFeedback(@RequestBody Map<String, Object> request) {
        Long userId = request.get("userId") != null ? Long.valueOf(request.get("userId").toString()) : null;
        String content = (String) request.get("content");
        log.debug("提交反馈, userId={}", userId);

        if (content == null || content.trim().isEmpty()) {
            return Result.error(400, "反馈内容不能为空");
        }

        Feedback feedback = new Feedback();
        feedback.setUserId(userId);
        feedback.setContent(content.trim());
        feedbackRepository.save(feedback);
        return Result.success("反馈提交成功", null);
    }

    @GetMapping("/{userId}")
    public Result<List<Feedback>> getUserFeedbacks(@PathVariable Long userId) {
        return Result.success(feedbackRepository.findByUserIdOrderByCreateTimeDesc(userId));
    }
}
