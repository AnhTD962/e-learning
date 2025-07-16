package com.domain.backend.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Đại diện cho một bộ sưu tập các Flashcard được liên kết với một bài học cụ thể.
 */
@Data // Lombok annotation để tự động tạo getters, setters, toString, equals, và hashCode
@Document(collection = "flashcardSets") // Chỉ định collection trong MongoDB
public class FlashcardSet {

    @Id // Đánh dấu trường này là ID chính của tài liệu
    private String id;

    private String title;
    private String description;

    @Field("lessonId") // Liên kết với ID của bài học
    private String lessonId;

    // Danh sách các Flashcard được nhúng trong bộ này
    private List<Flashcard> flashcards = new ArrayList<>();

    @CreatedDate // Tự động điền ngày tạo
    private LocalDateTime createdAt;

    @LastModifiedDate // Tự động điền ngày cập nhật cuối cùng
    private LocalDateTime updatedAt;

    // Phương thức tiện ích để cập nhật thời gian
    public void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}

