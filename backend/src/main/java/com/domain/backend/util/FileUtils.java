package com.domain.backend.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Lớp tiện ích cho các thao tác với file.
 * Cung cấp các phương thức để lưu trữ, tải và xóa file.
 */
@Component
public class FileUtils {

    private final Path fileStorageLocation;

    public FileUtils() {
        this.fileStorageLocation = Paths.get("./uploads").toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Không thể tạo thư mục lưu trữ file.", ex);
        }
    }

    /**
     * Lưu trữ một file tải lên.
     *
     * @param file File MultipartFile được tải lên.
     * @return Tên file đã lưu trữ.
     * @throws IOException nếu có lỗi trong quá trình lưu trữ file.
     */
    public String storeFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + "_" + org.springframework.util.StringUtils.cleanPath(file.getOriginalFilename());

        try {
            if (fileName.contains("..")) {
                throw new IOException("Tên file chứa các đường dẫn không hợp lệ " + fileName);
            }

            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new IOException("Không thể lưu trữ file " + fileName + ". Vui lòng thử lại!", ex);
        }
    }

    /**
     * Tải một file.
     *
     * @param fileName Tên file cần tải.
     * @return Đường dẫn tới file.
     */
    public Path loadFileAsResource(String fileName) {
        return this.fileStorageLocation.resolve(fileName).normalize();
    }

    /**
     * Xóa một file.
     *
     * @param fileName Tên file cần xóa.
     * @return True nếu xóa thành công, ngược lại là false.
     */
    public boolean deleteFile(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            return Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            System.err.println("Không thể xóa file " + fileName + ": " + ex.getMessage());
            return false;
        }
    }

    /**
     * Lấy kiểu MIME của một file.
     *
     * @param filePath Chuỗi đường dẫn tới file.
     * @return Kiểu MIME của file.
     * @throws IOException nếu có lỗi khi đọc kiểu nội dung.
     */
    public String getMimeType(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.probeContentType(path);
    }
}
