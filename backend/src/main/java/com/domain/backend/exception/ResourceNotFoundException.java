package com.domain.backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Ngoại lệ tùy chỉnh được ném khi một tài nguyên không được tìm thấy.
 * Nó ánh xạ tới mã trạng thái HTTP 404 (Not Found).
 */
@Getter
@ResponseStatus(HttpStatus.NOT_FOUND) // Đảm bảo Spring trả về 404 Not Found
public class ResourceNotFoundException extends RuntimeException {

    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s không tìm thấy với %s : '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

}
