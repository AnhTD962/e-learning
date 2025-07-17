package com.domain.backend.controller;

import com.domain.backend.dto.request.KanjiSearchRequest;
import com.domain.backend.dto.response.KanjiResponse;
import com.domain.backend.dto.response.MessageResponse;
import com.domain.backend.entity.KanjiEntry;
import com.domain.backend.service.KanjiService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/kanji") // Đường dẫn cơ sở cho các endpoint liên quan đến Kanji
public class KanjiController {

    @Autowired
    private KanjiService kanjiService;

    /**
     * Tạo một entry Kanji mới.
     * Chỉ ADMIN hoặc TEACHER mới có thể truy cập.
     *
     * @param kanjiEntry Đối tượng KanjiEntry cần tạo.
     * @return ResponseEntity với KanjiResponse đã tạo.
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    public ResponseEntity<KanjiResponse> createKanjiEntry(@Valid @RequestBody KanjiEntry kanjiEntry) {
        KanjiResponse createdKanji = kanjiService.createKanjiEntry(kanjiEntry);
        return new ResponseEntity<>(createdKanji, HttpStatus.CREATED);
    }

    /**
     * Lấy một entry Kanji theo ID.
     * Có thể truy cập bởi bất kỳ người dùng đã xác thực nào.
     *
     * @param id ID của entry Kanji.
     * @return ResponseEntity với KanjiResponse nếu tìm thấy, hoặc 404 Not Found.
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<KanjiResponse> getKanjiEntryById(@PathVariable String id) {
        return kanjiService.getKanjiEntryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Lấy một entry Kanji theo ký tự Kanji.
     * Có thể truy cập bởi bất kỳ người dùng đã xác thực nào.
     *
     * @param kanjiCharacter Ký tự Kanji.
     * @return ResponseEntity với KanjiResponse nếu tìm thấy, hoặc 404 Not Found.
     */
    @GetMapping("/character/{kanjiCharacter}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<KanjiResponse> getKanjiEntryByCharacter(@PathVariable String kanjiCharacter) {
        return kanjiService.getKanjiEntryByCharacter(kanjiCharacter)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Lấy tất cả các entry Kanji.
     * Có thể truy cập bởi bất kỳ người dùng đã xác thực nào.
     *
     * @return ResponseEntity với danh sách KanjiResponse.
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<KanjiResponse>> getAllKanjiEntries() {
        List<KanjiResponse> kanjiEntries = kanjiService.getAllKanjiEntries();
        return ResponseEntity.ok(kanjiEntries);
    }

    /**
     * Cập nhật một entry Kanji hiện có.
     * Chỉ ADMIN hoặc TEACHER mới có thể truy cập.
     *
     * @param id         ID của entry Kanji cần cập nhật.
     * @param kanjiEntry Đối tượng KanjiEntry với thông tin cập nhật.
     * @return ResponseEntity với KanjiResponse đã cập nhật.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    public ResponseEntity<KanjiResponse> updateKanjiEntry(@PathVariable String id, @Valid @RequestBody KanjiEntry kanjiEntry) {
        KanjiResponse updatedKanji = kanjiService.updateKanjiEntry(id, kanjiEntry);
        return ResponseEntity.ok(updatedKanji);
    }

    /**
     * Xóa một entry Kanji.
     * Chỉ ADMIN hoặc TEACHER mới có thể truy cập.
     *
     * @param id ID của entry Kanji cần xóa.
     * @return ResponseEntity với MessageResponse thành công.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    public ResponseEntity<MessageResponse> deleteKanjiEntry(@PathVariable String id) {
        MessageResponse response = kanjiService.deleteKanjiEntry(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Tìm kiếm các entry Kanji dựa trên truy vấn và loại tìm kiếm.
     * Có thể truy cập bởi bất kỳ người dùng đã xác thực nào.
     *
     * @param searchRequest Yêu cầu tìm kiếm Kanji.
     * @return ResponseEntity với danh sách KanjiResponse phù hợp.
     */
    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<KanjiResponse>> searchKanji(@Valid @ModelAttribute KanjiSearchRequest searchRequest) {
        List<KanjiResponse> results = kanjiService.searchKanji(searchRequest);
        return ResponseEntity.ok(results);
    }
}
