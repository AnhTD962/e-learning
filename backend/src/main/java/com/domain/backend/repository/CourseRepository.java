package com.domain.backend.repository;

import com.domain.backend.entity.Course;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository // Đánh dấu interface này là một Spring Data repository
public interface CourseRepository extends MongoRepository<Course, String> {
    // Các phương thức truy vấn tùy chỉnh có thể được thêm vào đây nếu cần, ví dụ: findByDifficultyLevel
}
