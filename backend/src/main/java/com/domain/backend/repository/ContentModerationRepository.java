package com.domain.backend.repository;

import com.domain.backend.entity.ContentModeration;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentModerationRepository extends MongoRepository<ContentModeration, String> {
    List<ContentModeration> findByEntityTypeAndEntityId(String entityType, String entityId);

    List<ContentModeration> findByModerationAction(String moderationAction);

    List<ContentModeration> findByReportedByUserId(String reportedByUserId);

    List<ContentModeration> findByModeratedByUserId(String moderatedByUserId);
}