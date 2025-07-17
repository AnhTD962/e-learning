package com.domain.backend.dto.response;

import com.domain.backend.entity.UserAnswer;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class QuizAttemptResponse {
    private String id;
    private String userId;
    private String quizId;
    private String lessonId;
    private int score;
    private int totalQuestions;
    private double percentageScore;
    private boolean passed;
    private List<UserAnswer> answers; // Có thể cần UserAnswerResponse nếu muốn tùy chỉnh
    private LocalDateTime submittedAt;
}