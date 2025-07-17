package com.domain.backend.dto.response;

import com.domain.backend.entity.QuestionOption;
import lombok.Data;

import java.util.List;

@Data
public class QuestionResponse {
    private String id;
    private String questionText;
    private String questionType;
    private List<QuestionOption> options; // Có thể cần một QuestionOptionResponse

}