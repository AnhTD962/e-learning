package com.domain.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserProfileUpdateRequest {
    @Size(min = 3, max = 20, message = "Tên người dùng phải từ 3 đến 20 ký tự")
    private String username;

    @Size(max = 50, message = "Email không được vượt quá 50 ký tự")
    @Email(message = "Email phải hợp lệ")
    private String email;

    // Thêm các trường khác mà người dùng có thể cập nhật, ví dụ:
    private String bio;
    private String profilePictureUrl;
}