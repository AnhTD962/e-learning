package com.domain.backend.exception;

import com.domain.backend.dto.response.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

/**
 * Lớp xử lý ngoại lệ toàn cục để quản lý các ngoại lệ trên toàn ứng dụng.
 * Nó cung cấp các phương thức để xử lý các loại ngoại lệ khác nhau và trả về phản hồi HTTP nhất quán.
 */
@ControllerAdvice // Cho phép lớp này xử lý các ngoại lệ trên toàn cầu
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Xử lý ngoại lệ ResourceNotFoundException.
     * Trả về 404 Not Found.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<MessageResponse> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        logger.error("Resource Not Found: {}", ex.getMessage());
        return new ResponseEntity<>(new MessageResponse(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    /**
     * Xử lý ngoại lệ UnauthorizedException.
     * Trả về 403 Forbidden.
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<MessageResponse> handleUnauthorizedException(UnauthorizedException ex, WebRequest request) {
        logger.error("Unauthorized Access: {}", ex.getMessage());
        return new ResponseEntity<>(new MessageResponse(ex.getMessage()), HttpStatus.FORBIDDEN);
    }

    /**
     * Xử lý ngoại lệ ValidationException.
     * Trả về 400 Bad Request.
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<MessageResponse> handleValidationException(ValidationException ex, WebRequest request) {
        logger.error("Validation Error: {}", ex.getMessage());
        return new ResponseEntity<>(new MessageResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    /**
     * Xử lý MethodArgumentNotValidException (lỗi xác thực @Valid trên DTOs).
     * Trả về 400 Bad Request với chi tiết lỗi xác thực.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        logger.error("Validation failed for request: {}", errors);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * Xử lý AuthenticationException và các lớp con của nó (ví dụ: BadCredentialsException).
     * Trả về 401 Unauthorized.
     */
    @ExceptionHandler({AuthenticationException.class, BadCredentialsException.class, DisabledException.class, LockedException.class})
    public ResponseEntity<MessageResponse> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
        logger.error("Authentication Error: {}", ex.getMessage());
        return new ResponseEntity<>(new MessageResponse("Xác thực thất bại: " + ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    /**
     * Xử lý AccessDeniedException (khi người dùng được xác thực nhưng không có quyền truy cập).
     * Trả về 403 Forbidden.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<MessageResponse> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        logger.error("Access Denied: {}", ex.getMessage());
        return new ResponseEntity<>(new MessageResponse("Truy cập bị từ chối: Bạn không có quyền thực hiện hành động này."), HttpStatus.FORBIDDEN);
    }

    /**
     * Xử lý ResponseStatusException (được ném thủ công với HttpStatus).
     * Trả về mã trạng thái và thông báo được chỉ định.
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<MessageResponse> handleResponseStatusException(ResponseStatusException ex, WebRequest request) {
        logger.error("Response Status Exception: {} - {}", ex.getStatusCode(), ex.getReason());
        return new ResponseEntity<>(new MessageResponse(ex.getReason()), ex.getStatusCode());
    }

    /**
     * Xử lý tất cả các ngoại lệ khác không được xử lý cụ thể.
     * Trả về 500 Internal Server Error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageResponse> handleGlobalException(Exception ex, WebRequest request) {
        logger.error("An unexpected error occurred: {}", ex.getMessage(), ex); // Log stack trace for unexpected errors
        return new ResponseEntity<>(new MessageResponse("Đã xảy ra lỗi nội bộ máy chủ. Vui lòng thử lại sau."), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}