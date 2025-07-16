package com.domain.backend.service;

import com.atilika.kuromoji.ipadic.Tokenizer;
import com.domain.backend.dto.response.MessageResponse;
import com.domain.backend.entity.VocabularyEntry;
import com.domain.backend.exception.ResourceNotFoundException;
import com.domain.backend.exception.UnauthorizedException;
import com.domain.backend.exception.ValidationException;
import com.domain.backend.repository.VocabularyEntryRepository;
import com.domain.backend.security.SecurityUtils;
import com.domain.backend.util.JapaneseTextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atilika.kuromoji.ipadic.Token;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Dịch vụ cung cấp các chức năng xử lý văn bản tiếng Nhật nâng cao,
 * bao gồm tạo furigana và quản lý từ vựng.
 * Đây là nơi tích hợp các thư viện NLP tiếng Nhật hoặc API bên ngoài.
 */
@Service
public class JapaneseTextService {

    @Autowired
    private JapaneseTextUtils japaneseTextUtils; // Tiện ích xử lý văn bản cơ bản

    @Autowired
    private VocabularyEntryRepository vocabularyEntryRepository; // Để quản lý từ vựng

    // Khởi tạo Kuromoji Tokenizer
    private final Tokenizer tokenizer = new Tokenizer();

    /**
     * Tạo furigana cho một chuỗi văn bản tiếng Nhật đã cho.
     * Sử dụng Kuromoji để phân tích cú pháp và trích xuất furigana.
     *
     * @param japaneseText Văn bản tiếng Nhật (có thể chứa Kanji).
     * @return Văn bản tiếng Nhật với furigana được thêm vào.
     */
    public String generateFuriganaForText(String japaneseText) {
        if (japaneseText == null || japaneseText.trim().isEmpty()) {
            return "";
        }

        List<Token> tokens = tokenizer.tokenize(japaneseText);
        StringBuilder furiganaBuilder = new StringBuilder();

        for (Token token : tokens) {
            String surface = token.getSurface(); // Từ gốc (Kanji/Kana)
            String reading = token.getReading(); // Âm đọc (Katakana)
            String baseForm = token.getBaseForm(); // Dạng cơ bản (nếu có)

            // Nếu từ gốc là Kanji và có âm đọc, thêm furigana
            if (japaneseTextUtils.containsKanji(surface) && reading != null && !reading.isEmpty()) {
                // Chuyển Katakana reading sang Hiragana cho furigana
                String hiraganaReading = japaneseTextUtils.katakanaToHiragana(reading);
                furiganaBuilder.append(surface).append(" (").append(hiraganaReading).append(")");
            } else {
                furiganaBuilder.append(surface); // Giữ nguyên nếu không phải Kanji hoặc không có reading
            }
        }
        return furiganaBuilder.toString();
    }

    /**
     * Lấy một entry từ vựng theo từ tiếng Nhật chính xác.
     *
     * @param japaneseWord Từ tiếng Nhật cần tìm (Kanji hoặc Kana).
     * @return Optional chứa VocabularyEntry nếu tìm thấy.
     */
    public Optional<VocabularyEntry> getVocabularyEntryByWord(String japaneseWord) {
        return vocabularyEntryRepository.findByJapaneseWord(japaneseWord);
    }

    /**
     * Tạo một entry từ vựng mới.
     * Chỉ ADMIN hoặc TEACHER mới có thể tạo.
     *
     * @param vocabularyEntry Đối tượng VocabularyEntry cần tạo.
     * @return VocabularyEntry đã tạo.
     * @throws ValidationException nếu từ tiếng Nhật đã tồn tại.
     * @throws UnauthorizedException nếu người dùng không được phép.
     */
    public VocabularyEntry createVocabularyEntry(VocabularyEntry vocabularyEntry) {
        if (!SecurityUtils.isAdmin() && !SecurityUtils.isTeacher()) {
            throw new UnauthorizedException("Bạn không được phép tạo entry từ vựng.");
        }
        if (vocabularyEntryRepository.findByJapaneseWord(vocabularyEntry.getJapaneseWord()).isPresent()) {
            throw new ValidationException("Từ tiếng Nhật '" + vocabularyEntry.getJapaneseWord() + "' đã tồn tại.");
        }
        return vocabularyEntryRepository.save(vocabularyEntry);
    }

    /**
     * Cập nhật một entry từ vựng hiện có.
     * Chỉ ADMIN hoặc TEACHER mới có thể cập nhật.
     *
     * @param id ID của entry từ vựng cần cập nhật.
     * @param updatedVocabularyEntry Đối tượng VocabularyEntry với thông tin cập nhật.
     * @return VocabularyEntry đã cập nhật.
     * @throws ResourceNotFoundException nếu không tìm thấy entry.
     * @throws UnauthorizedException nếu người dùng không được phép.
     */
    public VocabularyEntry updateVocabularyEntry(String id, VocabularyEntry updatedVocabularyEntry) {
        if (!SecurityUtils.isAdmin() && !SecurityUtils.isTeacher()) {
            throw new UnauthorizedException("Bạn không được phép cập nhật entry từ vựng.");
        }
        VocabularyEntry existingEntry = vocabularyEntryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vocabulary Entry", "id", id));

        // Cập nhật các trường được phép
        existingEntry.setJapaneseWord(updatedVocabularyEntry.getJapaneseWord());
        existingEntry.setFurigana(updatedVocabularyEntry.getFurigana());
        existingEntry.setRomaji(updatedVocabularyEntry.getRomaji());
        existingEntry.setMeaning(updatedVocabularyEntry.getMeaning());
        existingEntry.setExampleSentences(updatedVocabularyEntry.getExampleSentences());
        existingEntry.setPartOfSpeech(updatedVocabularyEntry.getPartOfSpeech());
        existingEntry.setJlptLevel(updatedVocabularyEntry.getJlptLevel());
        existingEntry.setAudioUrl(updatedVocabularyEntry.getAudioUrl());

        return vocabularyEntryRepository.save(existingEntry);
    }

    /**
     * Xóa một entry từ vựng.
     * Chỉ ADMIN hoặc TEACHER mới có thể xóa.
     *
     * @param id ID của entry từ vựng cần xóa.
     * @return MessageResponse thành công.
     * @throws ResourceNotFoundException nếu không tìm thấy entry.
     * @throws UnauthorizedException nếu người dùng không được phép.
     */
    public MessageResponse deleteVocabularyEntry(String id) {
        if (!SecurityUtils.isAdmin() && !SecurityUtils.isTeacher()) {
            throw new UnauthorizedException("Bạn không được phép xóa entry từ vựng.");
        }
        VocabularyEntry existingEntry = vocabularyEntryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vocabulary Entry", "id", id));
        vocabularyEntryRepository.delete(existingEntry);
        return new MessageResponse("Entry từ vựng đã xóa thành công!");
    }

    /**
     * Lấy tất cả các entry từ vựng.
     * @return Danh sách VocabularyEntry.
     */
    public List<VocabularyEntry> getAllVocabularyEntries() {
        return vocabularyEntryRepository.findAll();
    }

    /**
     * Populates some initial mock vocabulary data for testing purposes.
     * In a real application, this would be part of a data seeding process
     * or an import from a large dictionary dataset.
     * Chỉ ADMIN mới có thể thực hiện.
     */
    public MessageResponse populateMockVocabularyData() {
        if (!SecurityUtils.isAdmin()) {
            throw new UnauthorizedException("Bạn không được phép điền dữ liệu giả lập.");
        }
        if (vocabularyEntryRepository.count() > 0) {
            return new MessageResponse("Từ điển từ vựng đã được điền dữ liệu.");
        }

        List<VocabularyEntry> mockEntries = Arrays.asList(
                createVocabEntry("食べる", "たべる", "Taberu", "To eat", List.of("ご飯を食べる。", "寿司を食べたいです。"), "Verb", "N5"),
                createVocabEntry("飲む", "のむ", "Nomu", "To drink", List.of("水を飲む。", "コーヒーを飲みます。"), "Verb", "N5"),
                createVocabEntry("学生", "がくせい", "Gakusei", "Student", List.of("私は学生です。", "彼は優秀な学生です。"), "Noun", "N5"),
                createVocabEntry("先生", "せんせい", "Sensei", "Teacher", List.of("田中先生。", "先生に質問があります。"), "Noun", "N5"),
                createVocabEntry("日本語", "にほんご", "Nihongo", "Japanese language", List.of("日本語を勉強しています。", "日本語は難しいです。"), "Noun", "N5"),
                createVocabEntry("はい", "はい", "Hai", "Yes", List.of("はい、そうです。", "はい、お願いします。"), "Adverb", "N5"),
                createVocabEntry("いいえ", "いいえ", "Iie", "No", List.of("いいえ、違います。", "いいえ、結構です。"), "Adverb", "N5")
        );

        vocabularyEntryRepository.saveAll(mockEntries);
        return new MessageResponse("Dữ liệu từ vựng giả lập đã được điền thành công!");
    }

    private VocabularyEntry createVocabEntry(String japaneseWord, String furigana, String romaji, String meaning, List<String> exampleSentences, String partOfSpeech, String jlptLevel) {
        VocabularyEntry entry = new VocabularyEntry();
        entry.setJapaneseWord(japaneseWord);
        entry.setFurigana(furigana);
        entry.setRomaji(romaji);
        entry.setMeaning(meaning);
        entry.setExampleSentences(exampleSentences);
        entry.setPartOfSpeech(partOfSpeech);
        entry.setJlptLevel(jlptLevel);
        // entry.setAudioUrl("mock_audio_url_" + japaneseWord + ".mp3"); // Có thể thêm URL âm thanh giả lập
        return entry;
    }
}