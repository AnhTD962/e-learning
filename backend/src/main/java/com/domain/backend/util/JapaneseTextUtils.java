package com.domain.backend.util;

import org.springframework.stereotype.Component;

/**
 * Lớp tiện ích cho việc xử lý văn bản tiếng Nhật.
 * Trong một ứng dụng thực tế, lớp này sẽ tích hợp với một thư viện NLP tiếng Nhật mạnh mẽ
 * như Kuromoji, Mecab, hoặc các dịch vụ bên ngoài để chuyển đổi chính xác,
 * tạo furigana và phát hiện Romaji/Kana/Kanji.
 * <p>
 * Đối với ví dụ này, nó cung cấp logic chuyển đổi cơ bản, minh họa.
 */
@Component
public class JapaneseTextUtils {

    /**
     * Chuyển đổi Romaji sang Hiragana (rất cơ bản, để minh họa).
     * Đây là một ví dụ cực kỳ đơn giản hóa và sẽ cần một ánh xạ toàn diện
     * hoặc một thư viện chuyên dụng để sử dụng trong thực tế.
     */
    public String romajiToHiragana(String romaji) {
        // Mã giữ chỗ cho logic chuyển đổi Romaji sang Hiragana phức tạp
        // Ví dụ: "konnichiwa" -> "こんにちは"
        // Điều này thường liên quan đến một ánh xạ lớn hoặc một thư viện như Kuromoji.
        // Để minh họa, chúng ta sẽ chỉ trả về đầu vào hoặc một phép biến đổi đơn giản.
        switch (romaji.toLowerCase()) {
            case "konnichiwa":
                return "こんにちは";
            case "arigato":
                return "ありがとう";
            case "sayonara":
                return "さようなら";
            case "a":
                return "あ";
            case "i":
                return "い";
            case "u":
                return "う";
            case "e":
                return "え";
            case "o":
                return "お";
            default:
                return romaji; // Fallback
        }
    }

    /**
     * Chuyển đổi Hiragana sang Katakana (rất cơ bản, để minh họa).
     * Điều này có thể được thực hiện từng ký tự một với các offset Unicode.
     */
    public String hiraganaToKatakana(String hiragana) {
        StringBuilder katakana = new StringBuilder();
        for (char c : hiragana.toCharArray()) {
            if (c >= '\u3041' && c <= '\u3096') { // Phạm vi Hiragana
                katakana.append((char) (c + ('\u30AB' - '\u304B'))); // Offset sang Katakana
            } else {
                katakana.append(c);
            }
        }
        return katakana.toString();
    }

    /**
     * Chuyển đổi Katakana sang Hiragana (rất cơ bản, để minh họa).
     * Điều này có thể được thực hiện từng ký tự một với các offset Unicode.
     */
    public String katakanaToHiragana(String katakana) {
        StringBuilder hiragana = new StringBuilder();
        for (char c : katakana.toCharArray()) {
            if (c >= '\u30A1' && c <= '\u30F6') { // Phạm vi Katakana
                hiragana.append((char) (c - ('\u30AB' - '\u304B'))); // Offset sang Hiragana
            } else {
                hiragana.append(c);
            }
        }
        return hiragana.toString();
    }

    /**
     * Phát hiện loại script của một chuỗi tiếng Nhật đã cho.
     * Đây là một bộ phát hiện đơn giản hóa. Một bộ phát hiện thực tế sẽ sử dụng regex hoặc thư viện NLP.
     */
    public String detectScriptType(String text) {
        if (text == null || text.isEmpty()) {
            return "UNKNOWN";
        }
        boolean hasKanji = text.codePoints().anyMatch(cp -> cp >= 0x4E00 && cp <= 0x9FFF); // CJK Unified Ideographs
        boolean hasHiragana = text.codePoints().anyMatch(cp -> cp >= 0x3040 && cp <= 0x309F); // Hiragana
        boolean hasKatakana = text.codePoints().anyMatch(cp -> cp >= 0x30A0 && cp <= 0x30FF); // Katakana
        boolean hasRomaji = text.matches(".*[a-zA-Z].*"); // Chứa các ký tự Latin

        if (hasKanji) return "KANJI";
        if (hasHiragana) return "HIRAGANA";
        if (hasKatakana) return "KATAKANA";
        if (hasRomaji) return "ROMAJI";
        return "OTHER";
    }

    /**
     * Tạo furigana cho Kanji (mã giữ chỗ).
     * Đây là một tác vụ phức tạp yêu cầu một thư viện NLP tiếng Nhật.
     * Để minh họa, nó chỉ trả về đầu vào hoặc một furigana được mã hóa cứng.
     */
    public String generateFurigana(String japaneseText) {
        // Trong một ứng dụng thực tế, điều này sẽ sử dụng một thư viện như Kuromoji
        // hoặc một API bên ngoài để phân tích cú pháp Kanji và cung cấp furigana.
        if ("日本".equals(japaneseText)) return "にほん";
        if ("東京".equals(japaneseText)) return "とうきょう";
        return "furigana_placeholder"; // Fallback
    }

    /**
     * Kiểm tra xem một chuỗi có chứa ký tự Kanji hay không.
     *
     * @param text Chuỗi cần kiểm tra.
     * @return True nếu chuỗi chứa ít nhất một ký tự Kanji, ngược lại là false.
     */
    public boolean containsKanji(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        return text.codePoints().anyMatch(cp -> cp >= 0x4E00 && cp <= 0x9FFF);
    }

    /**
     * Kiểm tra xem một chuỗi có chứa ký tự Hiragana hay không.
     *
     * @param text Chuỗi cần kiểm tra.
     * @return True nếu chuỗi chứa ít nhất một ký tự Hiragana, ngược lại là false.
     */
    public boolean containsHiragana(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        return text.codePoints().anyMatch(cp -> cp >= 0x3040 && cp <= 0x309F);
    }

    /**
     * Kiểm tra xem một chuỗi có chứa ký tự Katakana hay không.
     *
     * @param text Chuỗi cần kiểm tra.
     * @return True nếu chuỗi chứa ít nhất một ký tự Katakana, ngược lại là false.
     */
    public boolean containsKatakana(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        return text.codePoints().anyMatch(cp -> cp >= 0x30A0 && cp <= 0x30FF);
    }

    /**
     * Kiểm tra xem một chuỗi có chứa ký tự Romaji (Latin) hay không.
     *
     * @param text Chuỗi cần kiểm tra.
     * @return True nếu chuỗi chứa ít nhất một ký tự Latin, ngược lại là false.
     */
    public boolean containsRomaji(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        return text.matches(".*[a-zA-Z].*");
    }
}