package com.domain.backend.service;

import com.domain.backend.dto.response.MessageResponse;
import com.domain.backend.exception.ResourceNotFoundException;
import com.domain.backend.exception.ValidationException;
import com.domain.backend.util.FileUtils;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;

/**
 * Dịch vụ quản lý việc lưu trữ và truy xuất file.
 */
@Service
public class FileStorageService {

    @Autowired
    private FileUtils fileUtils;

    /**
     * Tải lên một file và trả về URL truy cập.
     *
     * @param file File MultipartFile cần tải lên.
     * @return URL của file đã tải lên.
     * @throws ValidationException nếu file trống hoặc có lỗi lưu trữ.
     */
    public String uploadFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ValidationException("File không được trống.");
        }
        try {
            String fileName = fileUtils.storeFile(file);
            // Xây dựng URL truy cập file
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/files/download/")
                    .path(fileName)
                    .toUriString();
            return fileDownloadUri;
        } catch (IOException ex) {
            throw new ValidationException("Không thể tải lên file: " + ex.getMessage());
        }
    }

    /**
     * Tải xuống một file.
     *
     * @param fileName Tên file cần tải xuống.
     * @return ResponseEntity chứa Resource của file.
     * @throws ResourceNotFoundException nếu không tìm thấy file.
     */
    public ResponseEntity<Resource> downloadFile(String fileName) {
        try {
            Path filePath = fileUtils.loadFileAsResource(fileName);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                String contentType = null;
                try {
                    contentType = fileUtils.getMimeType(filePath.toString()); // Giả định FileUtils có phương thức này
                } catch (IOException ex) {
                    // Mặc định kiểu nội dung nếu không thể xác định
                    contentType = "application/octet-stream";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                throw new ResourceNotFoundException("File", "tên file", fileName);
            }
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("File", "tên file", fileName);
        }
    }

    /**
     * Xóa một file.
     *
     * @param fileName Tên file cần xóa.
     * @return MessageResponse thành công.
     */
    public MessageResponse deleteFile(String fileName) {
        if (fileUtils.deleteFile(fileName)) {
            return new MessageResponse("File đã xóa thành công!");
        } else {
            throw new ResourceNotFoundException("File", "tên file", fileName);
        }
    }
}
