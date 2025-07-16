package com.domain.backend.repository;

import com.domain.backend.entity.FlashcardSet;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Giao diện Repository để quản lý các hoạt động CRUD cho FlashcardSet trong MongoDB.
 */
@Repository // Đánh dấu giao diện này là một Spring Data Repository
public interface FlashcardSetRepository extends MongoRepository<FlashcardSet, String> {

    // Tìm một bộ Flashcard theo ID bài học liên quan của nó
    Optional<FlashcardSet> findByLessonId(String lessonId);
}
