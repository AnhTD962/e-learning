package com.domain.backend.repository;

import com.domain.backend.entity.Quiz;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // Đánh dấu interface này là một Spring Data repository
public interface QuizRepository extends MongoRepository<Quiz, String> {
    // Tìm một quiz theo ID bài học liên quan của nó
    Optional<Quiz> findByLessonId(String lessonId);
}
