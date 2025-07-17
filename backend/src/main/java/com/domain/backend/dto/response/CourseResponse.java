package com.domain.backend.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CourseResponse {
    private String id;
    private String title;
    private String description;
    private String difficultyLevel;
    private String createdByUserId;
    private List<ModuleResponse> modules;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}