package com.domain.backend.dto.response;

import lombok.Data;

import java.util.List;

@Data // Lombok annotation để tự động tạo getters, setters, toString, equals, và hashCode
public class ModuleResponse {
    private String id;
    private String title;
    private String description;
    private int orderIndex;
    private List<String> lessonIds; // Vẫn giữ là List<String> vì Lesson có DTO riêng
}
