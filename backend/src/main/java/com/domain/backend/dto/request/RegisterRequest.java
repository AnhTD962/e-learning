package com.domain.backend.dto.request;

import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data // Lombok annotation
public class RegisterRequest {
    @NotBlank(message = "Tên người dùng không được để trống")
    @Size(min = 3, max = 20, message = "Tên người dùng phải từ 3 đến 20 ký tự")
    private String username;

    @NotBlank(message = "Email không được để trống")
    @Size(max = 50, message = "Email không được vượt quá 50 ký tự")
    @Email(message = "Email phải hợp lệ")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, max = 40, message = "Mật khẩu phải từ 6 đến 40 ký tự")
    private String password;
}
