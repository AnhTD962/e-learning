package com.domain.backend.service;

import com.domain.backend.dto.request.CourseRequest;
import com.domain.backend.dto.request.ModuleRequest;
import com.domain.backend.dto.response.CourseResponse;
import com.domain.backend.dto.response.MessageResponse;
import com.domain.backend.dto.response.ModuleResponse;
import com.domain.backend.entity.Course;
import com.domain.backend.entity.CourseModule;
import com.domain.backend.exception.ResourceNotFoundException;
import com.domain.backend.exception.UnauthorizedException;
import com.domain.backend.repository.CourseRepository;
import com.domain.backend.security.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Dịch vụ xử lý logic nghiệp vụ liên quan đến quản lý khóa học.
 */
@Service // Đánh dấu lớp này là một Spring Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    /**
     * Chuyển đổi Course entity sang CourseResponse DTO.
     *
     * @param course Entity Course.
     * @return CourseResponse DTO.
     */
    private CourseResponse convertToCourseResponse(Course course) {
        CourseResponse response = new CourseResponse();
        BeanUtils.copyProperties(course, response);
        // Chuyển đổi danh sách CourseModule entities sang ModuleResponse DTOs
        if (course.getCourseModules() != null) {
            List<ModuleResponse> moduleResponses = course.getCourseModules().stream()
                    .map(this::convertToModuleResponse) // Gọi phương thức chuyển đổi mới
                    .collect(Collectors.toList());
            response.setModules(moduleResponses);
        } else {
            response.setModules(new ArrayList<>());
        }
        return response;
    }

    /**
     * Chuyển đổi CourseModule entity sang ModuleResponse DTO.
     *
     * @param courseModule Entity CourseModule.
     * @return ModuleResponse DTO.
     */
    private ModuleResponse convertToModuleResponse(CourseModule courseModule) {
        ModuleResponse response = new ModuleResponse();
        BeanUtils.copyProperties(courseModule, response);
        return response;
    }

    /**
     * Chuyển đổi ModuleRequest DTO sang CourseModule entity.
     *
     * @param moduleRequest DTO ModuleRequest.
     * @return Entity CourseModule đã chuyển đổi.
     */
    private CourseModule convertModuleRequestToModule(ModuleRequest moduleRequest) {
        CourseModule courseModule = new CourseModule();
        BeanUtils.copyProperties(moduleRequest, courseModule);
        // Đảm bảo rằng nếu có ID được cung cấp cho một courseModule hiện có, nó sẽ được sao chép.
        if (moduleRequest.getId() != null) {
            courseModule.setId(moduleRequest.getId());
        }
        return courseModule;
    }

    /**
     * Tạo một khóa học mới.
     *
     * @param courseRequest Payload yêu cầu để tạo khóa học.
     * @return Đối tượng CourseResponse đã tạo.
     * @throws UnauthorizedException nếu người dùng không được ủy quyền.
     */
    public CourseResponse createCourse(CourseRequest courseRequest) {
        // Kiểm tra ủy quyền: chỉ ADMIN hoặc TEACHER mới có thể tạo khóa học
        if (!SecurityUtils.isAdmin() && !SecurityUtils.isTeacher()) {
            throw new UnauthorizedException("Bạn không được phép tạo khóa học.");
        }

        Course course = new Course();
        BeanUtils.copyProperties(courseRequest, course); // Sao chép thuộc tính từ DTO sang entity

        // Đặt người tạo khóa học
        course.setCreatedByUserId(SecurityUtils.getCurrentUserId());

        // Chuyển đổi ModuleRequest sang đối tượng CourseModule
        if (courseRequest.getModules() != null && !courseRequest.getModules().isEmpty()) {
            List<CourseModule> courseModules = courseRequest.getModules().stream()
                    .map(this::convertModuleRequestToModule)
                    .collect(Collectors.toList());
            course.setCourseModules(courseModules);
        }

        Course savedCourse = courseRepository.save(course);
        return convertToCourseResponse(savedCourse);
    }

    /**
     * Lấy một khóa học theo ID của nó.
     *
     * @param id ID của khóa học.
     * @return Optional chứa CourseResponse nếu tìm thấy, rỗng nếu không.
     */
    public Optional<CourseResponse> getCourseById(String id) {
        return courseRepository.findById(id)
                .map(this::convertToCourseResponse);
    }

    /**
     * Lấy tất cả các khóa học.
     *
     * @return Danh sách tất cả các đối tượng CourseResponse.
     */
    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::convertToCourseResponse)
                .collect(Collectors.toList());
    }

    /**
     * Cập nhật một khóa học hiện có.
     *
     * @param id            ID của khóa học cần cập nhật.
     * @param courseRequest Payload yêu cầu để cập nhật khóa học.
     * @return Đối tượng CourseResponse đã cập nhật.
     * @throws ResourceNotFoundException nếu không tìm thấy khóa học.
     * @throws UnauthorizedException     nếu người dùng không được ủy quyền.
     */
    public CourseResponse updateCourse(String id, CourseRequest courseRequest) {
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khóa học", "id", id));

        // Chỉ người tạo hoặc ADMIN mới có thể cập nhật khóa học
        if (!existingCourse.getCreatedByUserId().equals(SecurityUtils.getCurrentUserId()) && !SecurityUtils.isAdmin()) {
            throw new UnauthorizedException("Bạn không được phép cập nhật khóa học này.");
        }

        BeanUtils.copyProperties(courseRequest, existingCourse, "id", "createdAt", "createdByUserId"); // Loại trừ ID, createdAt, createdByUserId khỏi việc sao chép

        // Xử lý cập nhật module: Đây là một cách tiếp cận đơn giản.
        // Đối với một giải pháp mạnh mẽ, bạn sẽ cần so sánh các module hiện có với các module mới
        // để xác định các bổ sung, cập nhật và xóa.
        if (courseRequest.getModules() != null) {
            List<CourseModule> updatedCourseModules = courseRequest.getModules().stream()
                    .map(this::convertModuleRequestToModule)
                    .collect(Collectors.toList());
            existingCourse.setCourseModules(updatedCourseModules);
        } else {
            existingCourse.setCourseModules(new ArrayList<>()); // Xóa module nếu không có gì được cung cấp
        }

        existingCourse.setUpdatedAt(); // Cập nhật dấu thời gian
        Course updatedCourse = courseRepository.save(existingCourse);
        return convertToCourseResponse(updatedCourse);
    }

    /**
     * Xóa một khóa học theo ID của nó.
     *
     * @param id ID của khóa học cần xóa.
     * @return ResponseEntity với thông báo thành công hoặc lỗi.
     * @throws ResourceNotFoundException nếu không tìm thấy khóa học.
     * @throws UnauthorizedException     nếu người dùng không được ủy quyền.
     */
    public MessageResponse deleteCourse(String id) {
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khóa học", "id", id));

        // Chỉ người tạo hoặc ADMIN mới có thể xóa khóa học
        if (!existingCourse.getCreatedByUserId().equals(SecurityUtils.getCurrentUserId()) && !SecurityUtils.isAdmin()) {
            throw new UnauthorizedException("Bạn không được phép xóa khóa học này.");
        }

        courseRepository.delete(existingCourse);
        return new MessageResponse("Khóa học đã xóa thành công!");
    }
}