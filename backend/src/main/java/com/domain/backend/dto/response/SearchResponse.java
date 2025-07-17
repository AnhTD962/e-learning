package com.domain.backend.dto.response;

import com.domain.backend.entity.VocabularyEntry;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data // Lombok annotation
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponse { // Đã đổi tên từ SearchResultResponse
    private String query;
    private String recognizedText; // Cho kết quả tìm kiếm chữ viết tay
    private List<KanjiResponse> kanjiResults; // Kết quả tìm kiếm Kanji
    private List<VocabularyEntry> vocabularyResults; // Kết quả tìm kiếm Từ vựng
    private List<LessonResponse> lessonResults; // Kết quả tìm kiếm Bài học
    private List<CourseResponse> courseResults; // Kết quả tìm kiếm Khóa học
    private String message;
}
