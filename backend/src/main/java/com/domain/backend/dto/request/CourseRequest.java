package com.domain.backend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data // Lombok annotation
public class CourseRequest {
    @NotBlank(message = "Tiêu đề khóa học không được để trống")
    @Size(min = 3, max = 100, message = "Tiêu đề khóa học phải từ 3 đến 100 ký tự")
    private String title;

    @NotBlank(message = "Mô tả khóa học không được để trống")
    @Size(max = 500, message = "Mô tả khóa học không được vượt quá 500 ký tự")
    private String description;

    @NotBlank(message = "Cấp độ khó không được để trống")
    private String difficultyLevel; // ví dụ: "BEGINNER", "INTERMEDIATE", "ADVANCED"

    @Valid // Xác thực danh sách các ModuleRequest
    private List<ModuleRequest> modules; // Để tạo/cập nhật các module cùng với khóa học
}