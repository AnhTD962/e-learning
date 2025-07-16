package com.domain.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Ngoại lệ tùy chỉnh được ném khi người dùng không được ủy quyền thực hiện một hành động.
 * Nó ánh xạ tới mã trạng thái HTTP 403 (Forbidden).
 */
@ResponseStatus(HttpStatus.FORBIDDEN) // Đảm bảo Spring trả về 403 Forbidden
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
