package com.domain.backend.controller;

import com.domain.backend.dto.request.LessonRequest;
import com.domain.backend.dto.response.LessonResponse;
import com.domain.backend.dto.response.MessageResponse;
import com.domain.backend.service.LessonService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api") // Đường dẫn cơ sở cho các endpoint chung
public class LessonController {

    @Autowired
    private LessonService lessonService;

    /**
     * Tạo một bài học mới trong một module cụ thể của một khóa học.
     * Chỉ ADMIN hoặc TEACHER mới có thể truy cập.
     *
     * @param courseId      ID của khóa học.
     * @param moduleId      ID của module.
     * @param lessonRequest Payload yêu cầu để tạo bài học.
     * @return ResponseEntity với LessonResponse đã tạo.
     */
    @PostMapping("/courses/{courseId}/modules/{moduleId}/lessons")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    public ResponseEntity<LessonResponse> createLesson(@PathVariable String courseId,
                                                       @PathVariable String moduleId,
                                                       @Valid @RequestBody LessonRequest lessonRequest) {
        LessonResponse createdLesson = lessonService.createLesson(courseId, moduleId, lessonRequest);
        return new ResponseEntity<>(createdLesson, HttpStatus.CREATED);
    }

    /**
     * Lấy một bài học theo ID.
     * Có thể truy cập bởi bất kỳ người dùng đã xác thực nào.
     *
     * @param lessonId ID của bài học.
     * @return ResponseEntity với LessonResponse nếu tìm thấy, hoặc 404 Not Found.
     */
    @GetMapping("/lessons/{lessonId}")
    @PreAuthorize("isAuthenticated()") // Hoặc hasAnyAuthority('ADMIN', 'TEACHER', 'STUDENT')
    public ResponseEntity<LessonResponse> getLessonById(@PathVariable String lessonId) {
        return lessonService.getLessonById(lessonId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Lấy tất cả các bài học cho một module cụ thể, sắp xếp theo orderIndex.
     * Có thể truy cập bởi bất kỳ người dùng đã xác thực nào.
     *
     * @param moduleId ID của module.
     * @return ResponseEntity với danh sách LessonResponse.
     */
    @GetMapping("/modules/{moduleId}/lessons")
    @PreAuthorize("isAuthenticated()") // Hoặc hasAnyAuthority('ADMIN', 'TEACHER', 'STUDENT')
    public ResponseEntity<List<LessonResponse>> getLessonsByModuleId(@PathVariable String moduleId) {
        List<LessonResponse> lessons = lessonService.getLessonsByModuleId(moduleId);
        return ResponseEntity.ok(lessons);
    }

    /**
     * Cập nhật một bài học hiện có.
     * Chỉ ADMIN hoặc TEACHER (người tạo khóa học) mới có thể truy cập.
     *
     * @param lessonId      ID của bài học cần cập nhật.
     * @param lessonRequest Payload yêu cầu để cập nhật bài học.
     * @return ResponseEntity với LessonResponse đã cập nhật.
     */
    @PutMapping("/lessons/{lessonId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')") // Kiểm tra thêm trong service
    public ResponseEntity<LessonResponse> updateLesson(@PathVariable String lessonId,
                                                       @Valid @RequestBody LessonRequest lessonRequest) {
        LessonResponse updatedLesson = lessonService.updateLesson(lessonId, lessonRequest);
        return ResponseEntity.ok(updatedLesson);
    }

    /**
     * Xóa một bài học theo ID.
     * Chỉ ADMIN hoặc TEACHER (người tạo khóa học) mới có thể truy cập.
     *
     * @param lessonId ID của bài học cần xóa.
     * @return ResponseEntity với MessageResponse thành công.
     */
    @DeleteMapping("/lessons/{lessonId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')") // Kiểm tra thêm trong service
    public ResponseEntity<MessageResponse> deleteLesson(@PathVariable String lessonId) {
        MessageResponse response = lessonService.deleteLesson(lessonId);
        return ResponseEntity.ok(response);
    }
}

