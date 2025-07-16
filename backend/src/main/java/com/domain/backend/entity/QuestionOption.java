package com.domain.backend.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

// QuestionOption được nhúng trong Question
@Data // Lombok annotation
public class QuestionOption { // Đã đổi tên từ Option thành QuestionOption

    @Id // @Id này dành cho tài liệu nhúng
    private String id;

    private String optionText;
    private boolean isCorrect;
}
