package com.domain.backend.repository;

import com.domain.backend.entity.Lesson;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository // Đánh dấu interface này là một Spring Data repository
public interface LessonRepository extends MongoRepository<Lesson, String> {

    // Phương thức tùy chỉnh để tìm các bài học theo moduleId và sắp xếp chúng
    List<Lesson> findByModuleIdOrderByOrderIndexAsc(String moduleId);

    // Phương thức tùy chỉnh để tìm các bài học theo courseId
    List<Lesson> findByCourseId(String courseId);
}