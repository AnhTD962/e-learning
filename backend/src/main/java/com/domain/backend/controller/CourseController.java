package com.domain.backend.controller;

import com.domain.backend.dto.request.CourseRequest;
import com.domain.backend.dto.response.CourseResponse;
import com.domain.backend.dto.response.MessageResponse;
import com.domain.backend.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/courses") // Đường dẫn cơ sở cho các endpoint liên quan đến khóa học
public class CourseController {

    @Autowired
    private CourseService courseService;

    /**
     * Tạo một khóa học mới.
     * Chỉ ADMIN hoặc TEACHER mới có thể truy cập.
     *
     * @param courseRequest Payload yêu cầu để tạo khóa học.
     * @return ResponseEntity với CourseResponse đã tạo.
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    public ResponseEntity<CourseResponse> createCourse(@Valid @RequestBody CourseRequest courseRequest) {
        CourseResponse createdCourse = courseService.createCourse(courseRequest);
        return new ResponseEntity<>(createdCourse, HttpStatus.CREATED);
    }

    /**
     * Lấy một khóa học theo ID.
     * Có thể truy cập bởi bất kỳ người dùng đã xác thực nào.
     *
     * @param id ID của khóa học.
     * @return ResponseEntity với CourseResponse nếu tìm thấy, hoặc 404 Not Found.
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()") // Hoặc hasAnyAuthority('ADMIN', 'TEACHER', 'STUDENT')
    public ResponseEntity<CourseResponse> getCourseById(@PathVariable String id) {
        return courseService.getCourseById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Lấy tất cả các khóa học.
     * Có thể truy cập bởi bất kỳ người dùng đã xác thực nào.
     *
     * @return ResponseEntity với danh sách CourseResponse.
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()") // Hoặc hasAnyAuthority('ADMIN', 'TEACHER', 'STUDENT')
    public ResponseEntity<List<CourseResponse>> getAllCourses() {
        List<CourseResponse> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }

    /**
     * Cập nhật một khóa học hiện có.
     * Chỉ ADMIN hoặc TEACHER (người tạo khóa học) mới có thể truy cập.
     *
     * @param id ID của khóa học cần cập nhật.
     * @param courseRequest Payload yêu cầu để cập nhật khóa học.
     * @return ResponseEntity với CourseResponse đã cập nhật.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')") // Kiểm tra thêm trong service
    public ResponseEntity<CourseResponse> updateCourse(@PathVariable String id, @Valid @RequestBody CourseRequest courseRequest) {
        CourseResponse updatedCourse = courseService.updateCourse(id, courseRequest);
        return ResponseEntity.ok(updatedCourse);
    }

    /**
     * Xóa một khóa học theo ID.
     * Chỉ ADMIN hoặc TEACHER (người tạo khóa học) mới có thể truy cập.
     *
     * @param id ID của khóa học cần xóa.
     * @return ResponseEntity với MessageResponse thành công.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')") // Kiểm tra thêm trong service
    public ResponseEntity<MessageResponse> deleteCourse(@PathVariable String id) {
        MessageResponse response = courseService.deleteCourse(id);
        return ResponseEntity.ok(response);
    }
}
