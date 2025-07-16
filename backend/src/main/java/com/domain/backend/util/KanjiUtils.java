package com.domain.backend.util;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Lớp tiện ích dành riêng cho các thao tác liên quan đến Kanji.
 * Trong một ứng dụng thực tế, điều này có thể bao gồm logic phức tạp hơn
 * để phân tích cú pháp, chuyển đổi, hoặc truy vấn dữ liệu Kanji.
 */
@Component
public class KanjiUtils {

    /**
     * Tạo dữ liệu SVG giả cho thứ tự nét của một ký tự Kanji.
     * Trong thực tế, bạn sẽ có một cơ sở dữ liệu hoặc một thư viện
     * cung cấp dữ liệu SVG thực tế cho từng ký tự Kanji.
     *
     * @param kanjiCharacter Ký tự Kanji.
     * @return Chuỗi SVG mô phỏng thứ tự nét.
     */
    public String generateMockStrokeOrderSvg(String kanjiCharacter) {
        // Đây là một hàm giả lập.
        // Dữ liệu SVG thực tế cho thứ tự nét Kanji rất phức tạp và lớn.
        // Bạn sẽ cần một nguồn dữ liệu bên ngoài (ví dụ: KanjiVG)
        // hoặc một thư viện để tạo/truy xuất nó.
        return "<svg width=\"100\" height=\"100\" viewBox=\"0 0 100 100\">" +
                "<rect x=\"10\" y=\"10\" width=\"80\" height=\"80\" stroke=\"black\" fill=\"none\"/>" +
                "<text x=\"50\" y=\"55\" font-family=\"Arial\" font-size=\"40\" text-anchor=\"middle\" dominant-baseline=\"middle\">" + kanjiCharacter + "</text>" +
                "<text x=\"50\" y=\"80\" font-family=\"Arial\" font-size=\"10\" text-anchor=\"middle\" dominant-baseline=\"middle\">Mock Stroke Order</text>" +
                "</svg>";
    }

    /**
     * Kiểm tra xem một ký tự có phải là Kanji hay không.
     * @param c Ký tự cần kiểm tra.
     * @return True nếu là Kanji, ngược lại là false.
     */
    public boolean isKanji(char c) {
        return c >= 0x4E00 && c <= 0x9FFF;
    }

    /**
     * Lấy danh sách các bộ thủ (radicals) của một Kanji (giả lập).
     * Đây cũng là một hàm giả lập. Dữ liệu thực tế cần một nguồn bên ngoài.
     * @param kanjiCharacter Ký tự Kanji.
     * @return Danh sách các bộ thủ.
     */
    public List<String> getMockRadicals(String kanjiCharacter) {
        // Dữ liệu bộ thủ là phức tạp và cần một nguồn đáng tin cậy.
        // Ví dụ đơn giản:
        if ("日".equals(kanjiCharacter)) return List.of("日");
        if ("本".equals(kanjiCharacter)) return List.of("木", "一");
        if ("語".equals(kanjiCharacter)) return List.of("言", "吾");
        return List.of("Bộ thủ giả lập");
    }
}