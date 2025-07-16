package com.domain.backend.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

// CourseModule được nhúng trong Course, nên không cần @Document
@Data // Lombok annotation
public class CourseModule {

    @Id // @Id này dành cho tài liệu nhúng, không phải ID collection cấp cao nhất
    private String id; // MongoDB sẽ tạo một ObjectId cho các tài liệu nhúng

    private String title;
    private String description;
    private int orderIndex; // Thứ tự các module trong một khóa học

    // Danh sách các ID bài học trong module này. Các bài học sẽ nằm trong collection riêng.
    private List<String> lessonIds = new ArrayList<>();
}