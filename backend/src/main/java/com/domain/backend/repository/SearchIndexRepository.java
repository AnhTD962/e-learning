package com.domain.backend.repository;

import com.domain.backend.entity.SearchIndex;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SearchIndexRepository extends MongoRepository<SearchIndex, String> {
    // Phương thức tùy chỉnh để tìm kiếm toàn văn.
    // Spring Data MongoDB sẽ tự động ánh xạ các trường @TextIndexed.
    // Để sử dụng tìm kiếm toàn văn, bạn sẽ cần tạo chỉ mục văn bản trên MongoDB:
    // db.searchIndex.createIndex({ title: "text", content: "text", keywords: "text" })
    List<SearchIndex> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseOrKeywordsContainingIgnoreCase(String title, String content, String keywords);

    // Có thể thêm các phương thức tìm kiếm khác dựa trên các trường cụ thể
    Optional<SearchIndex> findByEntityTypeAndEntityId(String entityType, String entityId);
    List<SearchIndex> findByEntityType(String entityType);
    List<SearchIndex> findByCourseId(String courseId);
    List<SearchIndex> findByJlptLevel(String jlptLevel);
}