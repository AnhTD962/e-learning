package com.domain.backend.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "quizAttempts") // Đã đổi tên collection từ userQuizAttempts thành quizAttempts
public class QuizAttempt { // Đã đổi tên từ UserQuizAttempt thành QuizAttempt

    @Id
    private String id;

    private String userId;
    private String quizId;
    private String lessonId; // Để tham chiếu nhanh đến bài học mà quiz này thuộc về

    private int score;
    private int totalQuestions;
    private double percentageScore;
    private boolean passed; // Dựa trên điểm đậu có thể cấu hình

    // Danh sách các câu trả lời của người dùng được nhúng cho mỗi câu hỏi trong quiz
    private List<UserAnswer> answers = new ArrayList<>();

    private LocalDateTime submittedAt;

    public QuizAttempt() {
        this.submittedAt = LocalDateTime.now();
    }
}