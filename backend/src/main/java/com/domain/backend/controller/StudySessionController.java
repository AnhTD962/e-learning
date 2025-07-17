package com.domain.backend.controller;

import com.domain.backend.dto.response.MessageResponse;
import com.domain.backend.entity.StudySession;
import com.domain.backend.service.StudySessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/study-sessions") // Đường dẫn cơ sở cho các endpoint liên quan đến phiên học
public class StudySessionController {

    @Autowired
    private StudySessionService studySessionService;

    /**
     * Bắt đầu một phiên học mới.
     * Có thể truy cập bởi bất kỳ người dùng đã xác thực nào.
     *
     * @param lessonId     Tùy chọn: ID của bài học đang học.
     * @param courseId     Tùy chọn: ID của khóa học đang học.
     * @param activityType Loại hoạt động (ví dụ: "LESSON_READING", "QUIZ", "FLASHCARD_REVIEW").
     * @return ResponseEntity với StudySession đã tạo.
     */
    @PostMapping("/start")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<StudySession> startStudySession(@RequestParam(required = false) String lessonId,
                                                          @RequestParam(required = false) String courseId,
                                                          @RequestParam String activityType) {
        StudySession session = studySessionService.startStudySession(lessonId, courseId, activityType);
        return new ResponseEntity<>(session, HttpStatus.CREATED);
    }

    /**
     * Kết thúc một phiên học.
     * Có thể truy cập bởi người dùng là chủ sở hữu phiên hoặc ADMIN.
     *
     * @param sessionId ID của phiên học cần kết thúc.
     * @return ResponseEntity với StudySession đã cập nhật.
     */
    @PostMapping("/end/{sessionId}")
    @PreAuthorize("isAuthenticated()") // Kiểm tra thêm trong service
    public ResponseEntity<StudySession> endStudySession(@PathVariable String sessionId) {
        StudySession updatedSession = studySessionService.endStudySession(sessionId);
        return ResponseEntity.ok(updatedSession);
    }

    /**
     * Lấy một phiên học theo ID.
     * Có thể truy cập bởi người dùng là chủ sở hữu phiên hoặc ADMIN.
     *
     * @param id ID của phiên học.
     * @return ResponseEntity với StudySession nếu tìm thấy, hoặc 404 Not Found.
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()") // Kiểm tra thêm trong service
    public ResponseEntity<StudySession> getStudySessionById(@PathVariable String id) {
        return studySessionService.getStudySessionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Lấy tất cả các phiên học của người dùng hiện tại.
     * Có thể truy cập bởi bất kỳ người dùng đã xác thực nào.
     *
     * @return ResponseEntity với danh sách StudySession.
     */
    @GetMapping("/my-sessions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<StudySession>> getMyStudySessions() {
        List<StudySession> sessions = studySessionService.getMyStudySessions();
        return ResponseEntity.ok(sessions);
    }

    /**
     * Lấy các phiên học của một người dùng cụ thể.
     * Chỉ ADMIN mới có thể truy cập.
     *
     * @param userId ID của người dùng.
     * @return ResponseEntity với danh sách StudySession.
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<StudySession>> getStudySessionsByUserId(@PathVariable String userId) {
        List<StudySession> sessions = studySessionService.getStudySessionsByUserId(userId);
        return ResponseEntity.ok(sessions);
    }

    /**
     * Xóa một phiên học.
     * Chỉ ADMIN mới có thể truy cập.
     *
     * @param id ID của phiên học cần xóa.
     * @return ResponseEntity với MessageResponse thành công.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<MessageResponse> deleteStudySession(@PathVariable String id) {
        MessageResponse response = studySessionService.deleteStudySession(id);
        return ResponseEntity.ok(response);
    }
}

