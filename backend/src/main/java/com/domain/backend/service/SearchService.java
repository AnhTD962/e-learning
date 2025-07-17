package com.domain.backend.service;

import com.domain.backend.dto.request.SearchRequest;
import com.domain.backend.dto.response.*;
import com.domain.backend.entity.*;
import com.domain.backend.exception.ValidationException;
import com.domain.backend.repository.CourseRepository;
import com.domain.backend.repository.KanjiEntryRepository;
import com.domain.backend.repository.LessonRepository;
import com.domain.backend.repository.VocabularyEntryRepository;
import com.domain.backend.util.JapaneseTextUtils;
import com.domain.backend.util.SearchUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service // Đánh dấu lớp này là một Spring Service
public class SearchService {

    @Autowired
    private KanjiEntryRepository kanjiEntryRepository;

    @Autowired
    private VocabularyEntryRepository vocabularyEntryRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private JapaneseTextUtils japaneseTextUtils;

    @Autowired
    private SearchUtils searchUtils;

    @Autowired
    private MongoTemplate mongoTemplate; // Để truy vấn phức tạp hơn

    // Đã xóa các @Value cho Google Cloud Vision API key và URL

    /**
     * Chuyển đổi KanjiEntry entity sang KanjiResponse DTO.
     *
     * @param kanjiEntry Entity KanjiEntry.
     * @return KanjiResponse DTO.
     */
    private KanjiResponse convertToKanjiResponse(KanjiEntry kanjiEntry) {
        KanjiResponse response = new KanjiResponse();
        BeanUtils.copyProperties(kanjiEntry, response);
        return response;
    }

    /**
     * Chuyển đổi Lesson entity sang LessonResponse DTO.
     *
     * @param lesson Entity Lesson.
     * @return LessonResponse DTO.
     */
    private LessonResponse convertToLessonResponse(Lesson lesson) {
        LessonResponse response = new LessonResponse();
        BeanUtils.copyProperties(lesson, response);
        return response;
    }

    /**
     * Chuyển đổi Course entity sang CourseResponse DTO.
     *
     * @param course Entity Course.
     * @return CourseResponse DTO.
     */
    private CourseResponse convertToCourseResponse(Course course) {
        CourseResponse response = new CourseResponse();
        BeanUtils.copyProperties(course, response);

        if (course.getCourseModules() != null) {
            List<ModuleResponse> moduleResponses = course.getCourseModules().stream()
                    .map(this::convertToModuleResponse)
                    .collect(Collectors.toList());
            response.setModules(moduleResponses);
        } else {
            response.setModules(List.of());
        }

        return response;
    }

    private ModuleResponse convertToModuleResponse(CourseModule courseModule) {
        ModuleResponse response = new ModuleResponse();
        BeanUtils.copyProperties(courseModule, response);
        return response;
    }

    /**
     * Thực hiện tìm kiếm văn bản đa script trên các entry từ điển, bài học và khóa học.
     *
     * @param searchRequest Yêu cầu tìm kiếm chứa truy vấn và loại script tùy chọn.
     * @return SearchResponse chứa các kết quả tìm kiếm phù hợp.
     * @throws ValidationException nếu truy vấn tìm kiếm trống.
     */
    public SearchResponse searchText(SearchRequest searchRequest) {
        String queryText = searchRequest.getQuery();
        String scriptType = searchRequest.getScriptType();

        if (queryText == null || queryText.trim().isEmpty()) {
            throw new ValidationException("Truy vấn tìm kiếm không được để trống.");
        }

        // Tạo tiêu chí tìm kiếm đa script
        Criteria searchCriteria = searchUtils.buildMultiScriptSearchCriteria(queryText, scriptType, japaneseTextUtils);

        // Tìm kiếm trong KanjiEntry
        Query kanjiQuery = new Query(searchCriteria);
        List<KanjiEntry> kanjiResults = mongoTemplate.find(kanjiQuery, KanjiEntry.class);

        // Tìm kiếm trong VocabularyEntry (sử dụng cùng tiêu chí)
        Query vocabQuery = new Query(searchCriteria);
        List<VocabularyEntry> vocabularyResults = mongoTemplate.find(vocabQuery, VocabularyEntry.class);

        // Tìm kiếm trong Lesson (ví dụ: theo tiêu đề hoặc nội dung)
        // Đây là một cách tiếp cận đơn giản; trong thực tế, bạn có thể muốn sử dụng SearchIndex
        // hoặc một thư viện tìm kiếm toàn văn như Elasticsearch.
        Query lessonQuery = new Query(new Criteria().orOperator(
                Criteria.where("title").regex(queryText, "i"),
                Criteria.where("content").regex(queryText, "i")
        ));
        List<Lesson> rawLessonResults = mongoTemplate.find(lessonQuery, Lesson.class);
        List<LessonResponse> lessonResponses = rawLessonResults.stream()
                .map(this::convertToLessonResponse)
                .collect(Collectors.toList());

        // Tìm kiếm trong Course (ví dụ: theo tiêu đề hoặc mô tả)
        Query courseQuery = new Query(new Criteria().orOperator(
                Criteria.where("title").regex(queryText, "i"),
                Criteria.where("description").regex(queryText, "i")
        ));
        List<Course> rawCourseResults = mongoTemplate.find(courseQuery, Course.class);
        List<CourseResponse> courseResponses = rawCourseResults.stream()
                .map(this::convertToCourseResponse)
                .collect(Collectors.toList());


        SearchResponse response = new SearchResponse();
        response.setQuery(queryText);
        response.setKanjiResults(kanjiResults.stream().map(this::convertToKanjiResponse).collect(Collectors.toList()));
        response.setVocabularyResults(vocabularyResults); // VocabularyEntry đã là DTO đủ tốt
        response.setLessonResults(lessonResponses);
        response.setCourseResults(courseResponses);
        response.setMessage("Tìm kiếm hoàn tất.");

        // Thêm dữ liệu giả lập nếu không có kết quả để minh họa
        if (kanjiResults.isEmpty() && vocabularyResults.isEmpty() && lessonResponses.isEmpty() && courseResponses.isEmpty()) {
            if (queryText.contains("日本")) {
                KanjiEntry mockKanji = new KanjiEntry();
                mockKanji.setKanjiCharacter("日本");
                mockKanji.setMeaning("Japan");
                mockKanji.setFurigana("にほん");
                response.getKanjiResults().add(convertToKanjiResponse(mockKanji));
            } else {
                VocabularyEntry mockVocab = new VocabularyEntry();
                mockVocab.setJapaneseWord("こんにちは");
                mockVocab.setMeaning("Hello");
                mockVocab.setFurigana("こんにちわ");
                response.getVocabularyResults().add(mockVocab);
            }
            response.setMessage("Tìm kiếm hoàn tất. (Dữ liệu giả lập)");
        }

        return response;
    }

    // Đã xóa phương thức searchHandwriting và parseRecognizedTextFromVisionApiResponse
}
