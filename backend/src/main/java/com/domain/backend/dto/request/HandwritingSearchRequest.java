package com.domain.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class HandwritingSearchRequest {
    @NotBlank(message = "Dữ liệu hình ảnh không được để trống")
    private String imageDataBase64; // Dữ liệu hình ảnh được mã hóa Base64 của ký tự đã vẽ
}
