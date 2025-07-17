package com.domain.backend.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AchievementResponse {
    private String id;
    private String name;
    private String description;
    private String iconUrl;
    private String criteria;
    private String type;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}