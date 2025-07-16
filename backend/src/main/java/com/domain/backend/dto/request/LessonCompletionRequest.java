package com.domain.backend.dto.request;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data // Lombok annotation
public class LessonCompletionRequest {
    @NotBlank(message = "ID bài học không được để trống")
    private String lessonId;
}
