package com.domain.backend.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "vocabularyEntries")
public class VocabularyEntry {

    @Id
    private String id;

    @Indexed
    private String japaneseWord; // Từ tiếng Nhật (Kanji hoặc Kana)
    private String furigana;     // Hiragana reading của Kanji
    private String romaji;       // Romaji reading
    private String meaning;      // Nghĩa tiếng Anh
    private List<String> exampleSentences; // Câu ví dụ sử dụng từ này
    private String partOfSpeech; // Loại từ (ví dụ: "Noun", "Verb", "Adjective")
    private String jlptLevel; // Cấp độ JLPT (ví dụ: "N5", "N4")
    private String audioUrl; // URL tới phát âm
}