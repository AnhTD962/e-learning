package com.domain.backend.dto.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class FlashcardSetResponse {
    private String id;
    private String title;
    private String description;
    private String lessonId;
    private List<FlashcardResponse> flashcards; // Danh sách các FlashcardResponse
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
