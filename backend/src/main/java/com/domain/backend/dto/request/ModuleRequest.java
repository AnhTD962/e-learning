package com.domain.backend.dto.request;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.util.List;

@Data // Lombok annotation
public class ModuleRequest {
    private String id; // Để cập nhật các module hiện có

    @NotBlank(message = "Tiêu đề module không được để trống")
    @Size(min = 3, max = 100, message = "Tiêu đề module phải từ 3 đến 100 ký tự")
    private String title;

    @NotBlank(message = "Mô tả module không được để trống")
    @Size(max = 500, message = "Mô tả module không được vượt quá 500 ký tự")
    private String description;

    @Min(value = 0, message = "Chỉ số thứ tự không được âm")
    private int orderIndex;

    // Khi tạo/cập nhật module, chúng ta có thể chỉ liên kết các ID bài học
    // Các đối tượng Lesson thực tế sẽ được quản lý trong collection riêng của chúng
    private List<String> lessonIds;
}
