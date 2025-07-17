package com.domain.backend.repository;

import com.domain.backend.entity.Achievement;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AchievementRepository extends MongoRepository<Achievement, String> {
    Optional<Achievement> findByName(String name);
}