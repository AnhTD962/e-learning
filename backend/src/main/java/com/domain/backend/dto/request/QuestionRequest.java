package com.domain.backend.dto.request;

import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Data // Lombok annotation
public class QuestionRequest {
    private String id; // Để cập nhật các câu hỏi hiện có

    @NotBlank(message = "Nội dung câu hỏi không được để trống")
    private String questionText;

    @NotBlank(message = "Loại câu hỏi không được để trống")
    private String questionType; // ví dụ: "MCQ", "FILL_BLANK"

    @Valid // Xác thực danh sách các OptionRequest
    private List<QuestionOptionRequest> options; // Cho MCQ (đã đổi tên từ OptionRequest)

    private String correctAnswer; // Cho FILL_BLANK
}