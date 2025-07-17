package com.domain.backend.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "achievements")
public class Achievement {

    @Id
    private String id;

    private String name;
    private String description;
    private String iconUrl; // URL tới biểu tượng thành tích
    private String criteria; // Mô tả tiêu chí để đạt được thành tích (ví dụ: "Hoàn thành 5 khóa học")
    private String type; // Loại thành tích (ví dụ: "COURSE_COMPLETION", "QUIZ_MASTER", "VOCAB_PRO")

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Achievement() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}