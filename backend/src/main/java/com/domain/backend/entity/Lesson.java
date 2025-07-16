package com.domain.backend.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data // Lombok annotation
@Document(collection = "lessons") // Ánh xạ lớp này tới collection "lessons" trong MongoDB
public class Lesson {

    @Id // Khóa chính trong MongoDB (_id)
    private String id;

    private String moduleId; // Tham chiếu tới CourseModule mà bài học này thuộc về
    private String courseId; // Tham chiếu tới Course mà bài học này thuộc về (để truy vấn dễ hơn)

    private String title;
    private String content; // Có thể lưu trữ nội dung Markdown hoặc HTML
    private String lessonType; // ví dụ: "TEXT", "QUIZ", "FLASHCARD", "VIDEO"
    private int orderIndex; // Thứ tự các bài học trong một module

    // Các trường tùy chọn cho các loại bài học cụ thể
    private String quizId; // Nếu lessonType là "QUIZ", tham chiếu tới tài liệu Quiz
    private String flashcardSetId; // Nếu lessonType là "FLASHCARD", tham chiếu tới tài liệu FlashcardSet
    private String videoUrl; // Nếu lessonType là "VIDEO"

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Lesson() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}
