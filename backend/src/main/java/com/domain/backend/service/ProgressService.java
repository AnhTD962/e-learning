package com.domain.backend.service;

import com.domain.backend.dto.request.LessonCompletionRequest;
import com.domain.backend.dto.response.ProgressResponse;
import com.domain.backend.entity.Course;
import com.domain.backend.entity.Lesson;
import com.domain.backend.entity.UserProgress;
import com.domain.backend.exception.ResourceNotFoundException;
import com.domain.backend.exception.ValidationException;
import com.domain.backend.repository.CourseRepository;
import com.domain.backend.repository.LessonRepository;
import com.domain.backend.repository.UserProgressRepository;
import com.domain.backend.security.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Dịch vụ xử lý logic nghiệp vụ liên quan đến theo dõi tiến độ người dùng.
 */
@Service
public class ProgressService {
    @Autowired
    private UserProgressRepository userProgressRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private LessonRepository lessonRepository;

    /**
     * Chuyển đổi UserProgress entity sang ProgressResponse DTO.
     * @param userProgress Entity UserProgress.
     * @return ProgressResponse DTO.
     */
    private ProgressResponse convertToProgressResponse(UserProgress userProgress) {
        ProgressResponse response = new ProgressResponse();
        BeanUtils.copyProperties(userProgress, response);
        return response;
    }

    /**
     * Đăng ký người dùng vào một khóa học bằng cách tạo hoặc cập nhật UserProgress của họ.
     *
     * @param courseId ID của khóa học để đăng ký.
     * @return Đối tượng ProgressResponse đã cập nhật hoặc mới tạo.
     * @throws ResourceNotFoundException nếu không tìm thấy khóa học.
     */
    public ProgressResponse enrollInCourse(String courseId) {
        String userId = SecurityUtils.getCurrentUserId();

        // Đảm bảo khóa học tồn tại
        courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Khóa học", "id", courseId));

        // Tìm tiến độ hiện có hoặc tạo mới
        Optional<UserProgress> existingProgress = userProgressRepository.findByUserIdAndCourseId(userId, courseId);

        UserProgress userProgress;
        if (existingProgress.isPresent()) {
            userProgress = existingProgress.get();
            // Nếu đã hoàn thành, đặt lại trạng thái thành đang tiến hành nếu đăng ký lại (logic tùy chọn)
            if ("COMPLETED".equals(userProgress.getStatus())) {
                userProgress.setStatus("IN_PROGRESS");
                userProgress.setCompletedAt(null); // Xóa ngày hoàn thành
            }
            userProgress.setLastAccessedAt();
        } else {
            userProgress = new UserProgress();
            userProgress.setUserId(userId);
            userProgress.setCourseId(courseId);
            // Trạng thái và phần trăm mặc định được đặt trong constructor
        }

        UserProgress savedProgress = userProgressRepository.save(userProgress);
        return convertToProgressResponse(savedProgress);
    }

    /**
     * Đánh dấu một bài học là đã hoàn thành cho người dùng trong một khóa học cụ thể.
     * Cập nhật tiến độ tổng thể của khóa học của người dùng.
     *
     * @param courseId ID của khóa học.
     * @param lessonCompletionRequest Yêu cầu chứa ID bài học để đánh dấu hoàn thành.
     * @return Đối tượng ProgressResponse đã cập nhật.
     * @throws ResourceNotFoundException nếu không tìm thấy khóa học/bài học/tiến độ người dùng.
     * @throws ValidationException nếu bài học không thuộc khóa học đã chỉ định.
     */
    public ProgressResponse markLessonComplete(String courseId, LessonCompletionRequest lessonCompletionRequest) {
        String userId = SecurityUtils.getCurrentUserId();
        String lessonId = lessonCompletionRequest.getLessonId();

        // 1. Lấy UserProgress cho khóa học
        UserProgress userProgress = userProgressRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Tiến độ người dùng", "khóa học", courseId + " cho người dùng " + userId));

        // 2. Đảm bảo Bài học tồn tại và thuộc khóa học đã chỉ định
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Bài học", "id", lessonId));

        if (!lesson.getCourseId().equals(courseId)) {
            throw new ValidationException("Bài học " + lessonId + " không thuộc khóa học " + courseId);
        }

        // 3. Thêm bài học vào danh sách đã hoàn thành nếu chưa có
        if (!userProgress.getCompletedLessonIds().contains(lessonId)) {
            userProgress.getCompletedLessonIds().add(lessonId);
        }

        // 4. Tính toán lại phần trăm tiến độ
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Khóa học", "id", courseId + " để tính toán tiến độ"));

        long totalLessonsInCourse = course.getCourseModules().stream()
                .flatMap(module -> module.getLessonIds().stream())
                .count();

        if (totalLessonsInCourse > 0) {
            double newProgressPercentage = (double) userProgress.getCompletedLessonIds().size() / totalLessonsInCourse * 100.0;
            userProgress.setProgressPercentage(Math.min(newProgressPercentage, 100.0)); // Giới hạn ở 100%
        } else {
            userProgress.setProgressPercentage(0.0); // Không có bài học trong khóa học
        }

        // 5. Cập nhật trạng thái nếu đã hoàn thành
        if (userProgress.getProgressPercentage() >= 100.0 && !"COMPLETED".equals(userProgress.getStatus())) {
            userProgress.setStatus("COMPLETED");
            userProgress.setCompletedAt(LocalDateTime.now());
        } else if (!"IN_PROGRESS".equals(userProgress.getStatus()) && userProgress.getProgressPercentage() > 0) {
            userProgress.setStatus("IN_PROGRESS");
        }

        userProgress.setLastAccessedLessonId(lessonId); // Cập nhật bài học được truy cập lần cuối
        userProgress.setLastAccessedAt(); // Cập nhật dấu thời gian truy cập lần cuối

        UserProgress savedProgress = userProgressRepository.save(userProgress);
        return convertToProgressResponse(savedProgress);
    }

    /**
     * Lấy tiến độ của người dùng cho một khóa học cụ thể.
     *
     * @param courseId ID của khóa học.
     * @return Optional chứa ProgressResponse nếu tìm thấy.
     */
    public Optional<ProgressResponse> getUserProgressForCourse(String courseId) {
        String userId = SecurityUtils.getCurrentUserId();
        return userProgressRepository.findByUserIdAndCourseId(userId, courseId)
                .map(this::convertToProgressResponse);
    }

    /**
     * Lấy tất cả các khóa học mà người dùng đã đăng ký và tiến độ của họ.
     *
     * @return Danh sách các đối tượng ProgressResponse cho người dùng hiện tại.
     */
    public List<ProgressResponse> getAllUserProgress() {
        String userId = SecurityUtils.getCurrentUserId();
        return userProgressRepository.findByUserId(userId).stream()
                .map(this::convertToProgressResponse)
                .collect(Collectors.toList());
    }
}
