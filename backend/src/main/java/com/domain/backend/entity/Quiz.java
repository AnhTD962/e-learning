package com.domain.backend.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data // Lombok annotation
@Document(collection = "quizzes") // Ánh xạ lớp này tới collection "quizzes" trong MongoDB
public class Quiz {

    @Id // Khóa chính trong MongoDB (_id)
    private String id;

    private String lessonId; // Tham chiếu tới Lesson mà quiz này thuộc về
    private String title;
    private String description;

    // Danh sách các câu hỏi được nhúng cho quiz này
    private List<Question> questions = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Quiz() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}
