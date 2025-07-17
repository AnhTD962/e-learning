package com.domain.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data // Lombok annotation
public class SearchRequest {
    @NotBlank(message = "Truy vấn tìm kiếm không được để trống")
    private String query;

    // Tùy chọn: Chỉ định loại script của truy vấn để gợi ý cho backend
    @Pattern(regexp = "KANJI|HIRAGANA|KATAKANA|ROMAJI|ALL", message = "Loại script không hợp lệ. Phải là KANJI, HIRAGANA, KATAKANA, ROMAJI, hoặc ALL.")
    private String scriptType = "ALL"; // Mặc định tìm kiếm trên tất cả các loại script
}