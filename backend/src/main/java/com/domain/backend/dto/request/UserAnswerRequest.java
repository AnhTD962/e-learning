package com.domain.backend.dto.request;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class UserAnswerRequest {
    @NotBlank(message = "ID câu hỏi không được để trống")
    private String questionId;

    private String selectedOptionId; // Cho MCQ
    private String submittedTextAnswer; // Cho FILL_BLANK
}