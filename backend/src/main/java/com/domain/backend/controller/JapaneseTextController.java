package com.domain.backend.controller;

import com.domain.backend.dto.response.MessageResponse;
import com.domain.backend.entity.VocabularyEntry;
import com.domain.backend.service.JapaneseTextService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/japanese-text") // Đường dẫn cơ sở cho các endpoint liên quan đến xử lý văn bản tiếng Nhật
public class JapaneseTextController {

    @Autowired
    private JapaneseTextService japaneseTextService;

    /**
     * Tạo furigana cho một chuỗi văn bản tiếng Nhật đã cho.
     * Có thể truy cập bởi bất kỳ người dùng đã xác thực nào.
     *
     * @param text Văn bản tiếng Nhật (có thể chứa Kanji).
     * @return ResponseEntity với văn bản tiếng Nhật có furigana.
     */
    @GetMapping("/furigana")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> generateFurigana(@RequestParam String text) {
        String furiganaText = japaneseTextService.generateFuriganaForText(text);
        return ResponseEntity.ok(furiganaText);
    }

    /**
     * Lấy một entry từ vựng theo từ tiếng Nhật chính xác.
     * Có thể truy cập bởi bất kỳ người dùng đã xác thực nào.
     *
     * @param word Từ tiếng Nhật cần tìm (Kanji hoặc Kana).
     * @return ResponseEntity với VocabularyEntry nếu tìm thấy, hoặc 404 Not Found.
     */
    @GetMapping("/vocabulary/lookup")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<VocabularyEntry> getVocabularyEntryByWord(@RequestParam String word) {
        return japaneseTextService.getVocabularyEntryByWord(word)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Lấy tất cả các entry từ vựng.
     * Có thể truy cập bởi bất kỳ người dùng đã xác thực nào.
     *
     * @return ResponseEntity với danh sách VocabularyEntry.
     */
    @GetMapping("/vocabulary")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<VocabularyEntry>> getAllVocabularyEntries() {
        List<VocabularyEntry> vocabularyEntries = japaneseTextService.getAllVocabularyEntries();
        return ResponseEntity.ok(vocabularyEntries);
    }

    /**
     * Tạo một entry từ vựng mới.
     * Chỉ ADMIN hoặc TEACHER mới có thể truy cập.
     *
     * @param vocabularyEntry Đối tượng VocabularyEntry cần tạo.
     * @return ResponseEntity với VocabularyEntry đã tạo.
     */
    @PostMapping("/vocabulary")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    public ResponseEntity<VocabularyEntry> createVocabularyEntry(@Valid @RequestBody VocabularyEntry vocabularyEntry) {
        VocabularyEntry createdEntry = japaneseTextService.createVocabularyEntry(vocabularyEntry);
        return new ResponseEntity<>(createdEntry, HttpStatus.CREATED);
    }

    /**
     * Cập nhật một entry từ vựng hiện có.
     * Chỉ ADMIN hoặc TEACHER mới có thể truy cập.
     *
     * @param id ID của entry từ vựng cần cập nhật.
     * @param vocabularyEntry Đối tượng VocabularyEntry với thông tin cập nhật.
     * @return ResponseEntity với VocabularyEntry đã cập nhật.
     */
    @PutMapping("/vocabulary/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    public ResponseEntity<VocabularyEntry> updateVocabularyEntry(@PathVariable String id, @Valid @RequestBody VocabularyEntry vocabularyEntry) {
        VocabularyEntry updatedEntry = japaneseTextService.updateVocabularyEntry(id, vocabularyEntry);
        return ResponseEntity.ok(updatedEntry);
    }

    /**
     * Xóa một entry từ vựng.
     * Chỉ ADMIN hoặc TEACHER mới có thể truy cập.
     *
     * @param id ID của entry từ vựng cần xóa.
     * @return ResponseEntity với MessageResponse thành công.
     */
    @DeleteMapping("/vocabulary/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    public ResponseEntity<MessageResponse> deleteVocabularyEntry(@PathVariable String id) {
        MessageResponse response = japaneseTextService.deleteVocabularyEntry(id);
        return ResponseEntity.ok(response);
    }
}
