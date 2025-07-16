package com.domain.backend.service;

import com.domain.backend.dto.request.KanjiSearchRequest;
import com.domain.backend.dto.response.KanjiResponse;
import com.domain.backend.dto.response.MessageResponse;
import com.domain.backend.entity.KanjiEntry;
import com.domain.backend.exception.ResourceNotFoundException;
import com.domain.backend.exception.UnauthorizedException;
import com.domain.backend.exception.ValidationException;
import com.domain.backend.repository.KanjiEntryRepository;
import com.domain.backend.security.SecurityUtils;
import com.domain.backend.util.JapaneseTextUtils;
import com.domain.backend.util.KanjiUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Dịch vụ xử lý logic nghiệp vụ liên quan đến quản lý và tìm kiếm các entry Kanji.
 */
@Service
public class KanjiService {

    @Autowired
    private KanjiEntryRepository kanjiEntryRepository;

    @Autowired
    private KanjiUtils kanjiUtils; // Để tạo SVG thứ tự nét, v.v.

    @Autowired
    private JapaneseTextUtils japaneseTextUtils; // Để phát hiện script

    /**
     * Chuyển đổi KanjiEntry entity sang KanjiResponse DTO.
     * @param kanjiEntry Entity KanjiEntry.
     * @return KanjiResponse DTO.
     */
    private KanjiResponse convertToKanjiResponse(KanjiEntry kanjiEntry) {
        KanjiResponse response = new KanjiResponse();
        BeanUtils.copyProperties(kanjiEntry, response);
        return response;
    }

    /**
     * Tạo một entry Kanji mới.
     * Chỉ ADMIN hoặc TEACHER mới có thể tạo.
     *
     * @param kanjiEntry Đối tượng KanjiEntry cần tạo.
     * @return KanjiResponse của entry đã tạo.
     * @throws ValidationException nếu ký tự Kanji đã tồn tại.
     * @throws UnauthorizedException nếu người dùng không được phép.
     */
    public KanjiResponse createKanjiEntry(KanjiEntry kanjiEntry) {
        if (!SecurityUtils.isAdmin() && !SecurityUtils.isTeacher()) {
            throw new UnauthorizedException("Bạn không được phép tạo entry Kanji.");
        }
        if (kanjiEntryRepository.findByKanjiCharacter(kanjiEntry.getKanjiCharacter()).isPresent()) {
            throw new ValidationException("Ký tự Kanji '" + kanjiEntry.getKanjiCharacter() + "' đã tồn tại.");
        }
        // Có thể tự động tạo strokeOrderSvg hoặc các trường khác ở đây
        if (kanjiEntry.getStrokeOrderSvg() == null || kanjiEntry.getStrokeOrderSvg().isEmpty()) {
            kanjiEntry.setStrokeOrderSvg(kanjiUtils.generateMockStrokeOrderSvg(kanjiEntry.getKanjiCharacter()));
        }
        KanjiEntry savedEntry = kanjiEntryRepository.save(kanjiEntry);
        return convertToKanjiResponse(savedEntry);
    }

    /**
     * Lấy một entry Kanji theo ID.
     * @param id ID của entry Kanji.
     * @return Optional chứa KanjiResponse nếu tìm thấy.
     */
    public Optional<KanjiResponse> getKanjiEntryById(String id) {
        return kanjiEntryRepository.findById(id)
                .map(this::convertToKanjiResponse);
    }

    /**
     * Lấy một entry Kanji theo ký tự Kanji.
     * @param kanjiCharacter Ký tự Kanji.
     * @return Optional chứa KanjiResponse nếu tìm thấy.
     */
    public Optional<KanjiResponse> getKanjiEntryByCharacter(String kanjiCharacter) {
        return kanjiEntryRepository.findByKanjiCharacter(kanjiCharacter)
                .map(this::convertToKanjiResponse);
    }

    /**
     * Lấy tất cả các entry Kanji.
     * @return Danh sách KanjiResponse.
     */
    public List<KanjiResponse> getAllKanjiEntries() {
        return kanjiEntryRepository.findAll().stream()
                .map(this::convertToKanjiResponse)
                .collect(Collectors.toList());
    }

    /**
     * Cập nhật một entry Kanji hiện có.
     * Chỉ ADMIN hoặc TEACHER mới có thể cập nhật.
     *
     * @param id ID của entry Kanji cần cập nhật.
     * @param updatedKanjiEntry Đối tượng KanjiEntry với thông tin cập nhật.
     * @return KanjiResponse của entry đã cập nhật.
     * @throws ResourceNotFoundException nếu không tìm thấy entry.
     * @throws UnauthorizedException nếu người dùng không được phép.
     */
    public KanjiResponse updateKanjiEntry(String id, KanjiEntry updatedKanjiEntry) {
        if (!SecurityUtils.isAdmin() && !SecurityUtils.isTeacher()) {
            throw new UnauthorizedException("Bạn không được phép cập nhật entry Kanji.");
        }
        KanjiEntry existingEntry = kanjiEntryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kanji Entry", "id", id));

        // Cập nhật các trường được phép
        existingEntry.setKanjiCharacter(updatedKanjiEntry.getKanjiCharacter());
        existingEntry.setOnyomi(updatedKanjiEntry.getOnyomi());
        existingEntry.setKunyomi(updatedKanjiEntry.getKunyomi());
        existingEntry.setMeaning(updatedKanjiEntry.getMeaning());
        existingEntry.setStrokeCount(updatedKanjiEntry.getStrokeCount());
        existingEntry.setExamples(updatedKanjiEntry.getExamples());
        existingEntry.setRadicals(updatedKanjiEntry.getRadicals());
        existingEntry.setJlptLevel(updatedKanjiEntry.getJlptLevel());
        // Có thể cập nhật strokeOrderSvg nếu được cung cấp, nếu không thì giữ nguyên
        if (updatedKanjiEntry.getStrokeOrderSvg() != null && !updatedKanjiEntry.getStrokeOrderSvg().isEmpty()) {
            existingEntry.setStrokeOrderSvg(updatedKanjiEntry.getStrokeOrderSvg());
        }

        KanjiEntry savedEntry = kanjiEntryRepository.save(existingEntry);
        return convertToKanjiResponse(savedEntry);
    }

    /**
     * Xóa một entry Kanji.
     * Chỉ ADMIN hoặc TEACHER mới có thể xóa.
     *
     * @param id ID của entry Kanji cần xóa.
     * @return MessageResponse thành công.
     * @throws ResourceNotFoundException nếu không tìm thấy entry.
     * @throws UnauthorizedException nếu người dùng không được phép.
     */
    public MessageResponse deleteKanjiEntry(String id) {
        if (!SecurityUtils.isAdmin() && !SecurityUtils.isTeacher()) {
            throw new UnauthorizedException("Bạn không được phép xóa entry Kanji.");
        }
        KanjiEntry existingEntry = kanjiEntryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kanji Entry", "id", id));
        kanjiEntryRepository.delete(existingEntry);
        return new MessageResponse("Entry Kanji đã xóa thành công!");
    }

    /**
     * Tìm kiếm các entry Kanji dựa trên truy vấn và loại tìm kiếm.
     *
     * @param searchRequest Yêu cầu tìm kiếm Kanji.
     * @return Danh sách các KanjiResponse phù hợp.
     * @throws ValidationException nếu truy vấn trống.
     */
    public List<KanjiResponse> searchKanji(KanjiSearchRequest searchRequest) {
        String query = searchRequest.getQuery();
        String searchType = searchRequest.getSearchType();

        if (query == null || query.trim().isEmpty()) {
            throw new ValidationException("Truy vấn tìm kiếm Kanji không được để trống.");
        }

        List<KanjiEntry> results = new ArrayList<>();
        String regexQuery = "(?i).*" + Pattern.quote(query) + ".*"; // Tìm kiếm không phân biệt chữ hoa chữ thường

        switch (searchType.toUpperCase()) {
            case "KANJI":
                results = kanjiEntryRepository.findByKanjiCharacter(query).map(List::of).orElse(List.of());
                break;
            case "ONYOMI":
                results = kanjiEntryRepository.findByOnyomiRegex(regexQuery, "i");
                break;
            case "KUNYOMI":
                results = kanjiEntryRepository.findByKunyomiRegex(regexQuery, "i");
                break;
            case "MEANING":
                results = kanjiEntryRepository.findByMeaningRegex(regexQuery, "i");
                break;
            case "ALL":
            default:
                // Tìm kiếm trên tất cả các trường liên quan
                Optional<KanjiEntry> byChar = kanjiEntryRepository.findByKanjiCharacter(query);
                byChar.ifPresent(results::add);
                results.addAll(kanjiEntryRepository.findByOnyomiRegex(regexQuery, "i"));
                results.addAll(kanjiEntryRepository.findByKunyomiRegex(regexQuery, "i"));
                results.addAll(kanjiEntryRepository.findByMeaningRegex(regexQuery, "i"));
                // Loại bỏ các bản sao nếu có
                results = results.stream().distinct().collect(Collectors.toList());
                break;
        }
        return results.stream()
                .map(this::convertToKanjiResponse)
                .collect(Collectors.toList());
    }
}
