package com.domain.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AnswerRequest {
    @NotBlank(message = "ID câu hỏi không được để trống")
    private String questionId;

    private String selectedOptionId; // Cho các câu hỏi trắc nghiệm
    private String submittedTextAnswer; // Cho các câu hỏi điền vào chỗ trống
}
