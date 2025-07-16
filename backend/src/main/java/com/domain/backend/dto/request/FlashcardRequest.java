package com.domain.backend.dto.request;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data // Lombok annotation
public class FlashcardRequest {
    private String id; // Để cập nhật các flashcard hiện có

    @NotBlank(message = "Văn bản mặt trước không được để trống")
    @Size(min = 1, max = 255, message = "Văn bản mặt trước phải từ 1 đến 255 ký tự")
    private String frontText;

    @NotBlank(message = "Văn bản mặt sau không được để trống")
    private String backText;

    private String furigana;
    private String romaji;
    private String exampleSentence;
    private String audioUrl;
    private String imageUrl;
}
