package com.domain.backend.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "studySessions")
public class StudySession {

    @Id
    private String id;

    private String userId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private long durationMinutes; // Thời lượng phiên học tính bằng phút
    private String lessonId; // Tùy chọn: bài học đang học
    private String courseId; // Tùy chọn: khóa học đang học
    private String activityType; // Ví dụ: "LESSON_READING", "QUIZ", "FLASHCARD_REVIEW", "WRITING_PRACTICE"

    private LocalDateTime createdAt;

    public StudySession() {
        this.createdAt = LocalDateTime.now();
    }
}