package com.domain.backend.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class QuizResponse {
    private String id;
    private String lessonId;
    private String title;
    private String description;
    private List<QuestionResponse> questions; // Sử dụng QuestionResponse
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}