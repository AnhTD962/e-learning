package com.domain.backend.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

// Question được nhúng trong Quiz
@Data // Lombok annotation
public class Question {

    @Id // @Id này dành cho tài liệu nhúng
    private String id;

    private String questionText;
    private String questionType; // ví dụ: "MCQ", "FILL_BLANK"

    // Cho các câu hỏi loại MCQ
    private List<QuestionOption> options = new ArrayList<>(); // Đã đổi tên từ Option thành QuestionOption

    // Cho các câu hỏi loại FILL_BLANK (chuỗi câu trả lời đúng)
    private String correctAnswer;
}