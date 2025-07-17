package com.domain.backend.controller;

import com.domain.backend.dto.request.LessonCompletionRequest;
import com.domain.backend.dto.response.ProgressResponse;
import com.domain.backend.service.ProgressService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/progress") // Đường dẫn cơ sở cho các endpoint liên quan đến tiến độ người dùng
public class ProgressController {

    @Autowired
    private ProgressService progressService;

    /**
     * Endpoint để người dùng đăng ký vào một khóa học.
     * Có thể truy cập bởi bất kỳ người dùng đã xác thực nào.
     *
     * @param courseId ID của khóa học để đăng ký.
     * @return ResponseEntity với ProgressResponse đã tạo/cập nhật.
     */
    @PostMapping("/enroll/{courseId}")
    @PreAuthorize("isAuthenticated()") // Hoặc hasAnyAuthority('STUDENT', 'TEACHER', 'ADMIN')
    public ResponseEntity<ProgressResponse> enrollInCourse(@PathVariable String courseId) {
        ProgressResponse userProgress = progressService.enrollInCourse(courseId);
        return new ResponseEntity<>(userProgress, HttpStatus.OK);
    }

    /**
     * Endpoint để người dùng đánh dấu một bài học là đã hoàn thành trong một khóa học.
     * Có thể truy cập bởi bất kỳ người dùng đã xác thực nào.
     *
     * @param courseId ID của khóa học.
     * @param request  Yêu cầu chứa ID bài học để đánh dấu hoàn thành.
     * @return ResponseEntity với ProgressResponse đã cập nhật.
     */
    @PostMapping("/courses/{courseId}/complete-lesson")
    @PreAuthorize("isAuthenticated()") // Hoặc hasAnyAuthority('STUDENT', 'TEACHER', 'ADMIN')
    public ResponseEntity<ProgressResponse> markLessonComplete(@PathVariable String courseId,
                                                               @Valid @RequestBody LessonCompletionRequest request) {
        ProgressResponse updatedProgress = progressService.markLessonComplete(courseId, request);
        return ResponseEntity.ok(updatedProgress);
    }

    /**
     * Lấy tiến độ của người dùng cho một khóa học cụ thể.
     * Có thể truy cập bởi bất kỳ người dùng đã xác thực nào.
     *
     * @param courseId ID của khóa học.
     * @return ResponseEntity với ProgressResponse nếu tìm thấy, hoặc 404 Not Found.
     */
    @GetMapping("/courses/{courseId}")
    @PreAuthorize("isAuthenticated()") // Hoặc hasAnyAuthority('STUDENT', 'TEACHER', 'ADMIN')
    public ResponseEntity<ProgressResponse> getUserProgressForCourse(@PathVariable String courseId) {
        return progressService.getUserProgressForCourse(courseId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Lấy tất cả các khóa học mà người dùng đã đăng ký và tiến độ của họ.
     * Có thể truy cập bởi bất kỳ người dùng đã xác thực nào.
     *
     * @return ResponseEntity với danh sách ProgressResponse cho người dùng hiện tại.
     */
    @GetMapping("/my-progress")
    @PreAuthorize("isAuthenticated()") // Hoặc hasAnyAuthority('STUDENT', 'TEACHER', 'ADMIN')
    public ResponseEntity<List<ProgressResponse>> getAllUserProgress() {
        List<ProgressResponse> userProgressList = progressService.getAllUserProgress();
        return ResponseEntity.ok(userProgressList);
    }
}
