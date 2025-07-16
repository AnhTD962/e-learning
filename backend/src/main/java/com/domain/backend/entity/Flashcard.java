package com.domain.backend.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * Đại diện cho một thẻ Flashcard duy nhất.
 * Đây là một đối tượng nhúng, không phải một tài liệu MongoDB cấp cao nhất riêng biệt.
 * Nó sẽ được chứa trong FlashcardSet.
 */
@Data
public class Flashcard {
    @Id // MongoDB sẽ tạo một ObjectId cho các tài liệu nhúng
    private String id;

    private String front; // Mặt trước của flashcard (ví dụ: từ tiếng Nhật)
    private String back;  // Mặt sau của flashcard (ví dụ: nghĩa, furigana)
    private int orderIndex;
}
