package com.domain.backend.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data // Lombok annotation
public class LessonRequest {
    @NotBlank(message = "Tiêu đề bài học không được để trống")
    @Size(min = 3, max = 100, message = "Tiêu đề bài học phải từ 3 đến 100 ký tự")
    private String title;

    @NotBlank(message = "Nội dung bài học không được để trống")
    private String content; // Có thể là Markdown hoặc HTML

    @NotBlank(message = "Loại bài học không được để trống")
    private String lessonType; // ví dụ: "TEXT", "QUIZ", "FLASHCARD", "VIDEO"

    @Min(value = 0, message = "Chỉ số thứ tự không được âm")
    private int orderIndex;

    // Các trường tùy chọn dựa trên loại bài học
    private String quizId;
    private String flashcardSetId;
    private String videoUrl;
}