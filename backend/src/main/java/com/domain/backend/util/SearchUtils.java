package com.domain.backend.util;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Lớp tiện ích cho việc xây dựng các tiêu chí tìm kiếm MongoDB.
 */
@Component
public class SearchUtils {

    /**
     * Xây dựng Criteria cho tìm kiếm đa script trong MongoDB.
     *
     * @param queryText         Văn bản truy vấn.
     * @param scriptType        Loại script (KANJI, HIRAGANA, KATAKANA, ROMAJI, ALL).
     * @param japaneseTextUtils Tiện ích chuyển đổi văn bản tiếng Nhật.
     * @return Đối tượng Criteria được xây dựng.
     */
    public Criteria buildMultiScriptSearchCriteria(String queryText, String scriptType, JapaneseTextUtils japaneseTextUtils) {
        List<Criteria> criteriaList = new ArrayList<>();

        // Luôn bao gồm tìm kiếm chính xác trên các trường liên quan
        criteriaList.add(Criteria.where("japaneseWord").regex(queryText, "i"));
        criteriaList.add(Criteria.where("furigana").regex(queryText, "i"));
        criteriaList.add(Criteria.where("romaji").regex(queryText, "i"));

        // Thêm các biến thể script nếu scriptType là ALL hoặc ROMAJI
        if ("ALL".equalsIgnoreCase(scriptType) || "ROMAJI".equalsIgnoreCase(scriptType)) {
            String hiraganaFromRomaji = japaneseTextUtils.romajiToHiragana(queryText);
            if (!hiraganaFromRomaji.equals(queryText)) { // Tránh thêm nếu không có chuyển đổi thực sự
                criteriaList.add(Criteria.where("furigana").regex(hiraganaFromRomaji, "i"));
                criteriaList.add(Criteria.where("japaneseWord").regex(hiraganaFromRomaji, "i")); // Có thể từ Romaji ra Hiragana rồi tìm Kanji
            }
            String katakanaFromRomaji = japaneseTextUtils.hiraganaToKatakana(hiraganaFromRomaji);
            if (!katakanaFromRomaji.equals(hiraganaFromRomaji)) {
                criteriaList.add(Criteria.where("furigana").regex(katakanaFromRomaji, "i")); // Có thể tìm Katakana trong furigana
                criteriaList.add(Criteria.where("japaneseWord").regex(katakanaFromRomaji, "i"));
            }
        }

        // Thêm các biến thể script nếu scriptType là ALL hoặc HIRAGANA/KATAKANA
        if ("ALL".equalsIgnoreCase(scriptType) || "HIRAGANA".equalsIgnoreCase(scriptType) || "KATAKANA".equalsIgnoreCase(scriptType)) {
            String hiragana = queryText;
            if ("KATAKANA".equalsIgnoreCase(scriptType)) {
                hiragana = japaneseTextUtils.katakanaToHiragana(queryText);
            }
            String katakana = japaneseTextUtils.hiraganaToKatakana(queryText);

            if (!hiragana.equals(queryText)) {
                criteriaList.add(Criteria.where("furigana").regex(hiragana, "i"));
                criteriaList.add(Criteria.where("japaneseWord").regex(hiragana, "i"));
            }
            if (!katakana.equals(queryText)) {
                criteriaList.add(Criteria.where("furigana").regex(katakana, "i"));
                criteriaList.add(Criteria.where("japaneseWord").regex(katakana, "i"));
            }
        }
        // Lưu ý: Chuyển đổi Kanji sang Kana/Romaji là phức tạp và thường được thực hiện bởi NLP library.
        // Nếu bạn có một trường 'reading' chung chứa cả furigana và romaji, việc tìm kiếm sẽ đơn giản hơn.

        return new Criteria().orOperator(criteriaList.toArray(new Criteria[0]));
    }
}