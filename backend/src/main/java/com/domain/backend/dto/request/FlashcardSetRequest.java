package com.domain.backend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data // Lombok annotation
public class FlashcardSetRequest {
    @NotBlank(message = "Tiêu đề bộ Flashcard không được để trống")
    @Size(min = 3, max = 100, message = "Tiêu đề bộ Flashcard phải từ 3 đến 100 ký tự")
    private String title;

    private String description;

    @Valid // Xác thực danh sách các FlashcardRequest
    private List<FlashcardRequest> flashcards;
}
