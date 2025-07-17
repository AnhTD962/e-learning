package com.domain.backend.repository;

import com.domain.backend.entity.StudySession;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StudySessionRepository extends MongoRepository<StudySession, String> {
    List<StudySession> findByUserId(String userId);

    List<StudySession> findByUserIdAndStartTimeBetween(String userId, LocalDateTime start, LocalDateTime end);

    List<StudySession> findByLessonId(String lessonId);

    List<StudySession> findByCourseId(String courseId);
}