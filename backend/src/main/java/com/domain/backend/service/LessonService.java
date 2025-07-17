package com.domain.backend.service;

import com.domain.backend.dto.request.LessonRequest;
import com.domain.backend.dto.response.LessonResponse;
import com.domain.backend.dto.response.MessageResponse;
import com.domain.backend.entity.Course;
import com.domain.backend.entity.CourseModule;
import com.domain.backend.entity.Lesson;
import com.domain.backend.exception.ResourceNotFoundException;
import com.domain.backend.exception.UnauthorizedException;
import com.domain.backend.repository.CourseRepository;
import com.domain.backend.repository.LessonRepository;
import com.domain.backend.security.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Dịch vụ xử lý logic nghiệp vụ liên quan đến quản lý bài học.
 */
@Service // Đánh dấu lớp này là một Spring Service
public class LessonService {

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private CourseRepository courseRepository; // Để cập nhật danh sách lessonIds của module

    /**
     * Kiểm tra xem người dùng hiện tại có được ủy quyền để quản lý một khóa học hay không (người tạo hoặc ADMIN).
     *
     * @param course Khóa học để kiểm tra ủy quyền.
     * @return True nếu được ủy quyền, ngược lại là false.
     */
    private boolean isAuthorizedToManageCourse(Course course) {
        return course.getCreatedByUserId().equals(SecurityUtils.getCurrentUserId()) || SecurityUtils.isAdmin();
    }

    /**
     * Chuyển đổi Lesson entity sang LessonResponse DTO.
     *
     * @param lesson Entity Lesson.
     * @return LessonResponse DTO.
     */
    private LessonResponse convertToLessonResponse(Lesson lesson) {
        LessonResponse response = new LessonResponse();
        BeanUtils.copyProperties(lesson, response);
        return response;
    }

    /**
     * Tạo một bài học mới trong một module cụ thể của một khóa học.
     *
     * @param courseId      ID của khóa học.
     * @param moduleId      ID của module.
     * @param lessonRequest Payload yêu cầu để tạo bài học.
     * @return Đối tượng LessonResponse đã tạo.
     * @throws ResourceNotFoundException nếu không tìm thấy khóa học/module.
     * @throws UnauthorizedException     nếu người dùng không được ủy quyền.
     */
    @Transactional // Đảm bảo tính nguyên tử cho các hoạt động liên quan đến nhiều tài liệu
    public LessonResponse createLesson(String courseId, String moduleId, LessonRequest lessonRequest) {
        // Tìm khóa học
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Khóa học", "id", courseId));

        // Kiểm tra ủy quyền
        if (!isAuthorizedToManageCourse(course)) {
            throw new UnauthorizedException("Bạn không được phép thêm bài học vào khóa học này.");
        }

        // Tìm courseModule trong khóa học
        // Corrected type from java.lang.CourseModule to com.domain.backend.entity.CourseModule
        Optional<CourseModule> optionalModule = course.getCourseModules().stream()
                .filter(m -> m.getId().equals(moduleId))
                .findFirst();

        if (optionalModule.isEmpty()) {
            throw new ResourceNotFoundException("CourseModule", "id", moduleId + " trong khóa học: " + courseId);
        }
        // Corrected type from java.lang.CourseModule to com.domain.backend.entity.CourseModule
        CourseModule courseModule = optionalModule.get();

        Lesson lesson = new Lesson();
        BeanUtils.copyProperties(lessonRequest, lesson); // Sao chép thuộc tính từ DTO sang entity
        lesson.setModuleId(moduleId);
        lesson.setCourseId(courseId);

        Lesson savedLesson = lessonRepository.save(lesson); // Lưu bài học vào collection riêng của nó

        // Thêm ID của bài học mới vào danh sách lessonIds của courseModule và lưu khóa học
        courseModule.getLessonIds().add(savedLesson.getId());
        course.setUpdatedAt(); // Cập nhật dấu thời gian khóa học
        courseRepository.save(course); // Lưu khóa học đã cập nhật với tham chiếu ID bài học mới

        return convertToLessonResponse(savedLesson);
    }

    /**
     * Lấy một bài học theo ID của nó.
     *
     * @param lessonId ID của bài học.
     * @return Optional chứa LessonResponse nếu tìm thấy, rỗng nếu không.
     */
    public Optional<LessonResponse> getLessonById(String lessonId) {
        return lessonRepository.findById(lessonId)
                .map(this::convertToLessonResponse);
    }

    /**
     * Lấy tất cả các bài học cho một module cụ thể, sắp xếp theo orderIndex.
     *
     * @param moduleId ID của module.
     * @return Danh sách các đối tượng LessonResponse.
     */
    public List<LessonResponse> getLessonsByModuleId(String moduleId) {
        return lessonRepository.findByModuleIdOrderByOrderIndexAsc(moduleId).stream()
                .map(this::convertToLessonResponse)
                .collect(Collectors.toList());
    }

    /**
     * Cập nhật một bài học hiện có.
     *
     * @param lessonId      ID của bài học cần cập nhật.
     * @param lessonRequest Payload yêu cầu để cập nhật bài học.
     * @return Đối tượng LessonResponse đã cập nhật.
     * @throws ResourceNotFoundException nếu không tìm thấy bài học hoặc khóa học liên quan.
     * @throws UnauthorizedException     nếu người dùng không được ủy quyền.
     */
    public LessonResponse updateLesson(String lessonId, LessonRequest lessonRequest) {
        Lesson existingLesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Bài học", "id", lessonId));

        // Tìm khóa học để kiểm tra ủy quyền
        Course course = courseRepository.findById(existingLesson.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Khóa học", "id", existingLesson.getCourseId() + " liên quan đến bài học: " + lessonId));

        if (!isAuthorizedToManageCourse(course)) {
            throw new UnauthorizedException("Bạn không được phép cập nhật bài học trong khóa học này.");
        }

        BeanUtils.copyProperties(lessonRequest, existingLesson, "id", "createdAt", "moduleId", "courseId"); // Loại trừ các trường không thay đổi
        existingLesson.setUpdatedAt(); // Cập nhật dấu thời gian
        Lesson updatedLesson = lessonRepository.save(existingLesson);
        return convertToLessonResponse(updatedLesson);
    }

    /**
     * Xóa một bài học theo ID của nó.
     *
     * @param lessonId ID của bài học cần xóa.
     * @return MessageResponse thành công.
     * @throws ResourceNotFoundException nếu không tìm thấy bài học hoặc khóa học liên quan.
     * @throws UnauthorizedException     nếu người dùng không được ủy quyền.
     */
    @Transactional
    public MessageResponse deleteLesson(String lessonId) {
        Lesson existingLesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Bài học", "id", lessonId));

        // Tìm khóa học để kiểm tra ủy quyền
        Course course = courseRepository.findById(existingLesson.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Khóa học", "id", existingLesson.getCourseId() + " liên quan đến bài học: " + lessonId));

        if (!isAuthorizedToManageCourse(course)) {
            throw new UnauthorizedException("Bạn không được phép xóa bài học khỏi khóa học này.");
        }

        // Xóa ID bài học khỏi danh sách lessonIds của module
        // Corrected type from java.lang.CourseModule to com.domain.backend.entity.CourseModule
        Optional<CourseModule> optionalModule = course.getCourseModules().stream()
                .filter(m -> m.getId().equals(existingLesson.getModuleId()))
                .findFirst();

        if (optionalModule.isPresent()) {
            // Corrected type from java.lang.CourseModule to com.domain.backend.entity.CourseModule
            CourseModule courseModule = optionalModule.get();
            courseModule.getLessonIds().remove(existingLesson.getId());
            course.setUpdatedAt(); // Cập nhật dấu thời gian khóa học
            courseRepository.save(course); // Lưu khóa học đã cập nhật
        }

        lessonRepository.delete(existingLesson); // Xóa tài liệu bài học
        return new MessageResponse("Bài học đã xóa thành công!");
    }
}