package com.domain.backend.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LessonResponse {
    private String id;
    private String moduleId;
    private String courseId;
    private String title;
    private String content;
    private String lessonType;
    private int orderIndex;
    private String quizId;
    private String flashcardSetId;
    private String videoUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
