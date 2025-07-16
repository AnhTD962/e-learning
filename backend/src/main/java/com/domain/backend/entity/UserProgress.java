package com.domain.backend.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data // Lombok annotation
@Document(collection = "userProgress") // Ánh xạ lớp này tới collection "userProgress" trong MongoDB
@CompoundIndexes({
        @CompoundIndex(name = "user_course_idx", def = "{'userId': 1, 'courseId': 1}", unique = true)
}) // Đảm bảo một người dùng chỉ có một tài liệu tiến độ cho mỗi khóa học
public class UserProgress {

    @Id // Khóa chính trong MongoDB (_id)
    private String id;

    private String userId; // Tham chiếu tới User
    private String courseId; // Tham chiếu tới Course

    private double progressPercentage; // Tổng tiến độ khóa học (0.0 đến 100.0)
    private String status; // ví dụ: "ENROLLED", "IN_PROGRESS", "COMPLETED"

    // Lưu trữ các ID bài học đã hoàn thành cho khóa học này
    private Set<String> completedLessonIds = new HashSet<>();

    // Lưu trữ ID bài học được truy cập lần cuối cho khóa học này
    private String lastAccessedLessonId;

    private LocalDateTime enrolledAt;
    private LocalDateTime lastAccessedAt;
    private LocalDateTime completedAt; // Dấu thời gian khi khóa học được hoàn thành

    public UserProgress() {
        this.enrolledAt = LocalDateTime.now();
        this.lastAccessedAt = LocalDateTime.now();
        this.progressPercentage = 0.0;
        this.status = "ENROLLED";
    }

    // Phương thức để cập nhật dấu thời gian lastAccessedAt
    public void setLastAccessedAt() {
        this.lastAccessedAt = LocalDateTime.now();
    }
}
