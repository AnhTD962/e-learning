package com.domain.backend.repository;

import com.domain.backend.entity.UserProgress;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository // Đánh dấu interface này là một Spring Data repository
public interface UserProgressRepository extends MongoRepository<UserProgress, String> {

    // Phương thức tùy chỉnh để tìm tiến độ người dùng cho một khóa học cụ thể
    Optional<UserProgress> findByUserIdAndCourseId(String userId, String courseId);

    // Phương thức tùy chỉnh để tìm tất cả tiến độ cho một người dùng cụ thể
    List<UserProgress> findByUserId(String userId);
}