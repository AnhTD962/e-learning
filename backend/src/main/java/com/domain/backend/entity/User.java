package com.domain.backend.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data // Lombok annotation để tự động tạo getters, setters, toString, equals, và hashCode
@Document(collection = "users") // Ánh xạ lớp này tới collection "users" trong MongoDB
public class User {

    @Id // Đánh dấu trường này là khóa chính trong MongoDB (_id)
    private String id;

    private String username;

    @Indexed(unique = true) // Đảm bảo địa chỉ email là duy nhất trong collection
    private String email;

    private String passwordHash; // Lưu trữ mật khẩu đã băm

    private String profileImage;

    // Sử dụng Set để lưu trữ các vai trò cho tính linh hoạt (ví dụ: "STUDENT", "ADMIN", "TEACHER")
    private Set<String> roles = new HashSet<>();

    private Set<String> grantedAchievementIds = new HashSet<>();

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Constructor để tự động đặt dấu thời gian tạo và cập nhật
    public User() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Phương thức để cập nhật dấu thời gian updatedAt
    public void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}
