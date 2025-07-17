package com.domain.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data // Lombok annotation
public class QuestionOptionRequest { // Đã đổi tên từ OptionRequest
    private String id; // Để cập nhật các tùy chọn hiện có

    @NotBlank(message = "Nội dung tùy chọn không được để trống")
    private String optionText;

    @NotNull(message = "isCorrect phải được chỉ định")
    private Boolean isCorrect;
}