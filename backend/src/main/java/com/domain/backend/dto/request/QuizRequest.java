package com.domain.backend.dto.request;

import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

@Data // Lombok annotation
public class QuizRequest {
    @NotBlank(message = "Tiêu đề Quiz không được để trống")
    @Size(min = 3, max = 100, message = "Tiêu đề Quiz phải từ 3 đến 100 ký tự")
    private String title;

    private String description;

    @Valid // Xác thực danh sách các QuestionRequest
    private List<QuestionRequest> questions;
}