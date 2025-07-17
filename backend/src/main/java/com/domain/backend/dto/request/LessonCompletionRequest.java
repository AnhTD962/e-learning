package com.domain.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data // Lombok annotation
public class LessonCompletionRequest {
    @NotBlank(message = "ID bài học không được để trống")
    private String lessonId;
}
