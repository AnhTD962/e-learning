package com.domain.backend.service;

import com.domain.backend.dto.response.MessageResponse;
import com.domain.backend.entity.StudySession;
import com.domain.backend.exception.ResourceNotFoundException;
import com.domain.backend.exception.UnauthorizedException;
import com.domain.backend.exception.ValidationException;
import com.domain.backend.repository.StudySessionRepository;
import com.domain.backend.security.SecurityUtils;
import com.domain.backend.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Dịch vụ xử lý logic nghiệp vụ liên quan đến các phiên học của người dùng.
 */
@Service
public class StudySessionService {

    @Autowired
    private StudySessionRepository studySessionRepository;

    @Autowired
    private DateUtils dateUtils; // Để tính toán thời lượng

    /**
     * Bắt đầu một phiên học mới.
     *
     * @param lessonId Tùy chọn: ID của bài học đang học.
     * @param courseId Tùy chọn: ID của khóa học đang học.
     * @param activityType Loại hoạt động (ví dụ: "LESSON_READING", "QUIZ", "FLASHCARD_REVIEW").
     * @return StudySession đã tạo.
     */
    public StudySession startStudySession(String lessonId, String courseId, String activityType) {
        String userId = SecurityUtils.getCurrentUserId();
        StudySession session = new StudySession();
        session.setUserId(userId);
        session.setStartTime(LocalDateTime.now());
        session.setLessonId(lessonId);
        session.setCourseId(courseId);
        session.setActivityType(activityType);
        return studySessionRepository.save(session);
    }

    /**
     * Kết thúc một phiên học.
     *
     * @param sessionId ID của phiên học cần kết thúc.
     * @return StudySession đã cập nhật.
     * @throws ResourceNotFoundException nếu không tìm thấy phiên học.
     * @throws UnauthorizedException nếu người dùng không phải là chủ sở hữu phiên hoặc ADMIN.
     */
    public StudySession endStudySession(String sessionId) {
        StudySession session = studySessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Phiên học", "id", sessionId));

        // Chỉ chủ sở hữu phiên hoặc ADMIN mới có thể kết thúc phiên
        if (!session.getUserId().equals(SecurityUtils.getCurrentUserId()) && !SecurityUtils.isAdmin()) {
            throw new UnauthorizedException("Bạn không được phép kết thúc phiên học này.");
        }

        if (session.getEndTime() != null) {
            throw new ValidationException("Phiên học đã kết thúc.");
        }

        session.setEndTime(LocalDateTime.now());
        session.setDurationMinutes(dateUtils.calculateDurationInMinutes(session.getStartTime(), session.getEndTime()));
        return studySessionRepository.save(session);
    }

    /**
     * Lấy một phiên học theo ID.
     *
     * @param id ID của phiên học.
     * @return Optional chứa StudySession nếu tìm thấy.
     */
    public Optional<StudySession> getStudySessionById(String id) {
        return studySessionRepository.findById(id);
    }

    /**
     * Lấy tất cả các phiên học của người dùng hiện tại.
     *
     * @return Danh sách StudySession của người dùng hiện tại.
     */
    public List<StudySession> getMyStudySessions() {
        String userId = SecurityUtils.getCurrentUserId();
        return studySessionRepository.findByUserId(userId);
    }

    /**
     * Lấy các phiên học của một người dùng cụ thể (chỉ ADMIN).
     *
     * @param userId ID của người dùng.
     * @return Danh sách StudySession của người dùng.
     * @throws UnauthorizedException nếu người dùng không phải là ADMIN.
     */
    public List<StudySession> getStudySessionsByUserId(String userId) {
        if (!SecurityUtils.isAdmin()) {
            throw new UnauthorizedException("Bạn không được phép xem phiên học của người dùng khác.");
        }
        return studySessionRepository.findByUserId(userId);
    }

    /**
     * Xóa một phiên học.
     * Chỉ ADMIN mới có thể xóa.
     *
     * @param id ID của phiên học cần xóa.
     * @return MessageResponse thành công.
     * @throws ResourceNotFoundException nếu không tìm thấy phiên học.
     * @throws UnauthorizedException nếu người dùng không phải là ADMIN.
     */
    public MessageResponse deleteStudySession(String id) {
        if (!SecurityUtils.isAdmin()) {
            throw new UnauthorizedException("Bạn không được phép xóa phiên học.");
        }
        StudySession session = studySessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Phiên học", "id", id));
        studySessionRepository.delete(session);
        return new MessageResponse("Phiên học đã xóa thành công!");
    }
}
