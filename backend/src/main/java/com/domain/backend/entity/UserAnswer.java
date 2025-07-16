package com.domain.backend.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

// UserAnswer được nhúng trong QuizAttempt
@Data
public class UserAnswer {

    @Id
    private String id;

    private String questionId; // Tham chiếu tới câu hỏi trong quiz
    private String selectedOptionId; // Cho MCQ: ID của tùy chọn đã chọn
    private String submittedTextAnswer; // Cho FILL_BLANK: Câu trả lời văn bản đã gửi của người dùng

    private boolean isCorrect;
}
