package com.domain.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserAnswerRequest {
    @NotBlank(message = "ID câu hỏi không được để trống")
    private String questionId;

    private String selectedOptionId; // Cho MCQ
    private String submittedTextAnswer; // Cho FILL_BLANK
}