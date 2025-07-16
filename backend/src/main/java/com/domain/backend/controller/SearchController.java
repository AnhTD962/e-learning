package com.domain.backend.controller;

import com.domain.backend.dto.request.SearchRequest;
import com.domain.backend.dto.response.SearchResponse;
import com.domain.backend.service.SearchService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/search") // Đường dẫn cơ sở cho các endpoint liên quan đến tìm kiếm
public class SearchController {

    @Autowired
    private SearchService searchService;

    /**
     * Thực hiện tìm kiếm văn bản đa script cho các từ/cụm từ tiếng Nhật.
     * Có thể truy cập bởi bất kỳ người dùng đã xác thực nào.
     *
     * @param searchRequest Yêu cầu chứa chuỗi truy vấn và loại script tùy chọn.
     * @return ResponseEntity với kết quả tìm kiếm.
     */
    @GetMapping("/text")
    @PreAuthorize("isAuthenticated()") // Hoặc hasAnyAuthority('ADMIN', 'TEACHER', 'STUDENT')
    public ResponseEntity<SearchResponse> searchText(@Valid @ModelAttribute SearchRequest searchRequest) {
        // Sử dụng @ModelAttribute cho các yêu cầu GET với các đối tượng phức tạp
        SearchResponse response = searchService.searchText(searchRequest);
        return ResponseEntity.ok(response);
    }
}
