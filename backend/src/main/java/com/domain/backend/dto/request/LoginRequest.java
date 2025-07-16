package com.domain.backend.dto.request;

import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data // Lombok annotation
public class LoginRequest {
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email phải hợp lệ")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;
}