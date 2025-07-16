package com.domain.backend.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.TextIndexed;

import java.util.List;

@Data
@Document(collection = "searchIndex")
public class SearchIndex {

    @Id
    private String id;

    private String entityType; // Loại thực thể (ví dụ: "COURSE", "LESSON", "KANJI", "VOCABULARY")
    private String entityId;   // ID của thực thể gốc

    @TextIndexed(weight = 5) // Đánh chỉ mục văn bản với trọng số cao cho các trường chính
    private String title;

    @TextIndexed(weight = 3)
    private String content; // Nội dung chính của thực thể (ví dụ: mô tả khóa học, nội dung bài học)

    @TextIndexed(weight = 2)
    private String keywords; // Các từ khóa liên quan (ví dụ: "N5", "động từ", "chào hỏi")

    private List<String> scriptForms; // Các dạng chữ viết (Kanji, Hiragana, Katakana, Romaji) của nội dung chính

    // Có thể thêm các trường khác để lọc hoặc sắp xếp kết quả tìm kiếm
    private String courseId; // Nếu entityType là LESSON, QUIZ, FLASHCARD
    private String difficultyLevel; // Nếu entityType là COURSE
    private String jlptLevel; // Nếu entityType là KANJI, VOCABULARY
}