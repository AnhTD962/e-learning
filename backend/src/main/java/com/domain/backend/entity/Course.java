package com.domain.backend.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data // Lombok annotation
@Document(collection = "courses") // Ánh xạ lớp này tới collection "courses" trong MongoDB
public class Course {

    @Id // Khóa chính trong MongoDB (_id)
    private String id;

    private String title;
    private String description;
    private String difficultyLevel; // ví dụ: "BEGINNER", "INTERMEDIATE", "ADVANCED"
    private String createdByUserId; // Tham chiếu tới User đã tạo khóa học

    // Danh sách các CourseModule được nhúng. CourseModule không phải là collection riêng.
    private List<CourseModule> courseModules = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Course() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}
