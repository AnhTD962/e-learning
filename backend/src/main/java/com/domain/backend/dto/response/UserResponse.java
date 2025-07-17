package com.domain.backend.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class UserResponse {
    private String id;
    private String username;
    private String email;
    private Set<String> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // Thêm các trường khác nếu cần hiển thị trong phản hồi người dùng
    private String bio;
    private String profilePictureUrl;
}