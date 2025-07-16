package com.domain.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Ngoại lệ tùy chỉnh được ném khi có lỗi xác thực dữ liệu.
 * Nó ánh xạ tới mã trạng thái HTTP 400 (Bad Request).
 */
@ResponseStatus(HttpStatus.BAD_REQUEST) // Đảm bảo Spring trả về 400 Bad Request
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}