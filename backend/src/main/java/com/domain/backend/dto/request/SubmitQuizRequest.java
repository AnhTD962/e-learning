package com.domain.backend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class SubmitQuizRequest {
    @NotBlank(message = "ID Quiz không được để trống")
    private String quizId;

    @NotEmpty(message = "Câu trả lời không được để trống")
    @Valid
    private List<UserAnswerRequest> answers;
}