package com.domain.backend.controller;

import com.domain.backend.dto.request.QuizRequest;
import com.domain.backend.dto.request.SubmitQuizRequest;
import com.domain.backend.dto.response.MessageResponse;
import com.domain.backend.dto.response.QuizAttemptResponse;
import com.domain.backend.dto.response.QuizResponse;
import com.domain.backend.service.QuizService;
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
public class QuizController {

    @Autowired
    private QuizService quizService;

    /**
     * Tạo một quiz mới cho một bài học cụ thể.
     * Chỉ ADMIN hoặc TEACHER mới có thể truy cập.
     *
     * @param lessonId ID của bài học để liên kết quiz với.
     * @param quizRequest Payload yêu cầu để tạo quiz.
     * @return ResponseEntity với QuizResponse đã tạo.
     */
    @PostMapping("/lessons/{lessonId}/quizzes")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    public ResponseEntity<QuizResponse> createQuiz(@PathVariable String lessonId,
                                                   @Valid @RequestBody QuizRequest quizRequest) {
        QuizResponse createdQuiz = quizService.createQuiz(lessonId, quizRequest);
        return new ResponseEntity<>(createdQuiz, HttpStatus.CREATED);
    }

    /**
     * Lấy một quiz theo ID của nó.
     * Có thể truy cập bởi bất kỳ người dùng đã xác thực nào.
     *
     * @param quizId ID của quiz.
     * @return ResponseEntity với QuizResponse nếu tìm thấy, hoặc 404 Not Found.
     */
    @GetMapping("/quizzes/{quizId}")
    @PreAuthorize("isAuthenticated()") // Hoặc hasAnyAuthority('ADMIN', 'TEACHER', 'STUDENT')
    public ResponseEntity<QuizResponse> getQuizById(@PathVariable String quizId) {
        return quizService.getQuizById(quizId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Lấy một quiz theo ID bài học liên quan của nó.
     * Có thể truy cập bởi bất kỳ người dùng đã xác thực nào.
     *
     * @param lessonId ID của bài học.
     * @return ResponseEntity với QuizResponse nếu tìm thấy, hoặc 404 Not Found.
     */
    @GetMapping("/lessons/{lessonId}/quiz")
    @PreAuthorize("isAuthenticated()") // Hoặc hasAnyAuthority('ADMIN', 'TEACHER', 'STUDENT')
    public ResponseEntity<QuizResponse> getQuizByLessonId(@PathVariable String lessonId) {
        return quizService.getQuizByLessonId(lessonId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Cập nhật một quiz hiện có.
     * Chỉ ADMIN hoặc TEACHER mới có thể truy cập.
     *
     * @param quizId ID của quiz cần cập nhật.
     * @param quizRequest Payload yêu cầu để cập nhật quiz.
     * @return ResponseEntity với QuizResponse đã cập nhật.
     */
    @PutMapping("/quizzes/{quizId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    public ResponseEntity<QuizResponse> updateQuiz(@PathVariable String quizId,
                                                   @Valid @RequestBody QuizRequest quizRequest) {
        QuizResponse updatedQuiz = quizService.updateQuiz(quizId, quizRequest);
        return ResponseEntity.ok(updatedQuiz);
    }

    /**
     * Xóa một quiz theo ID của nó.
     * Chỉ ADMIN hoặc TEACHER mới có thể truy cập.
     *
     * @param quizId ID của quiz cần xóa.
     * @return ResponseEntity với MessageResponse thành công.
     */
    @DeleteMapping("/quizzes/{quizId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    public ResponseEntity<MessageResponse> deleteQuiz(@PathVariable String quizId) {
        MessageResponse response = quizService.deleteQuiz(quizId);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint để người dùng nộp một quiz.
     * Có thể truy cập bởi bất kỳ người dùng đã xác thực nào.
     *
     * @param submitQuizRequest Yêu cầu chứa ID quiz và câu trả lời của người dùng.
     * @return ResponseEntity với QuizAttemptResponse kết quả.
     */
    @PostMapping("/quizzes/submit")
    @PreAuthorize("isAuthenticated()") // Hoặc hasAnyAuthority('STUDENT', 'TEACHER', 'ADMIN')
    public ResponseEntity<QuizAttemptResponse> submitQuiz(@Valid @RequestBody SubmitQuizRequest submitQuizRequest) {
        QuizAttemptResponse attempt = quizService.submitQuiz(submitQuizRequest);
        return new ResponseEntity<>(attempt, HttpStatus.CREATED);
    }

    /**
     * Lấy tất cả các lần thử quiz của người dùng hiện tại.
     * Có thể truy cập bởi bất kỳ người dùng đã xác thực nào.
     *
     * @return ResponseEntity với danh sách QuizAttemptResponse.
     */
    @GetMapping("/my-quiz-attempts")
    @PreAuthorize("isAuthenticated()") // Hoặc hasAnyAuthority('STUDENT', 'TEACHER', 'ADMIN')
    public ResponseEntity<List<QuizAttemptResponse>> getUserQuizAttempts() {
        List<QuizAttemptResponse> attempts = quizService.getUserQuizAttempts();
        return ResponseEntity.ok(attempts);
    }

    /**
     * Lấy một lần thử quiz cụ thể theo ID của nó.
     * Có thể truy cập bởi bất kỳ người dùng đã xác thực nào.
     *
     * @param attemptId ID của lần thử quiz.
     * @return ResponseEntity với QuizAttemptResponse nếu tìm thấy, hoặc 404 Not Found.
     */
    @GetMapping("/quiz-attempts/{attemptId}")
    @PreAuthorize("isAuthenticated()") // Hoặc hasAnyAuthority('STUDENT', 'TEACHER', 'ADMIN')
    public ResponseEntity<QuizAttemptResponse> getQuizAttemptById(@PathVariable String attemptId) {
        return quizService.getQuizAttemptById(attemptId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}