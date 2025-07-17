package com.domain.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class KanjiSearchRequest {
    @NotBlank(message = "Truy vấn Kanji không được để trống")
    private String query; // Có thể là ký tự Kanji, âm đọc hoặc nghĩa

    @Pattern(regexp = "KANJI|ONYOMI|KUNYOMI|MEANING|ALL", message = "Loại tìm kiếm Kanji không hợp lệ. Phải là KANJI, ONYOMI, KUNYOMI, MEANING, hoặc ALL.")
    private String searchType = "ALL"; // Mặc định tìm kiếm trên tất cả các trường
}