package com.domain.backend.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class ProgressResponse {
    private String id;
    private String userId;
    private String courseId;
    private double progressPercentage;
    private String status;
    private Set<String> completedLessonIds;
    private String lastAccessedLessonId;
    private LocalDateTime enrolledAt;
    private LocalDateTime lastAccessedAt;
    private LocalDateTime completedAt;
}