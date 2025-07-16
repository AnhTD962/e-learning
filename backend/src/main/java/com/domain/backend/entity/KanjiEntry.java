package com.domain.backend.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "kanjiEntries")
public class KanjiEntry {

    @Id
    private String id;

    @Indexed(unique = true)
    private String kanjiCharacter; // Ký tự Kanji
    private String onyomi; // Âm đọc On'yomi
    private String kunyomi; // Âm đọc Kun'yomi
    private String meaning; // Nghĩa tiếng Anh
    private  String furigana; // Furigana
    private int strokeCount; // Số nét
    private String strokeOrderSvg; // Dữ liệu SVG cho thứ tự nét
    private List<String> examples; // Các từ ví dụ sử dụng Kanji này
    private List<String> radicals; // Các bộ thủ cấu thành Kanji
    private String jlptLevel; // Cấp độ JLPT (ví dụ: "N5", "N4")
}