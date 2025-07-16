package com.domain.backend.util;

import com.domain.backend.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Lớp tiện ích cho các chức năng xác thực chung.
 */
@Component
public class ValidationUtils {

    // Regex cơ bản cho email
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$"
    );

    /**
     * Xác thực định dạng email.
     * @param email Chuỗi email cần xác thực.
     * @throws ValidationException nếu email không hợp lệ.
     */
    public void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email không được để trống.");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException("Định dạng email không hợp lệ.");
        }
    }

    /**
     * Xác thực độ dài mật khẩu.
     * @param password Chuỗi mật khẩu cần xác thực.
     * @param minLength Độ dài tối thiểu.
     * @param maxLength Độ dài tối đa.
     * @throws ValidationException nếu mật khẩu không đáp ứng yêu cầu độ dài.
     */
    public void validatePasswordLength(String password, int minLength, int maxLength) {
        if (password == null || password.isEmpty()) {
            throw new ValidationException("Mật khẩu không được để trống.");
        }
        if (password.length() < minLength || password.length() > maxLength) {
            throw new ValidationException(String.format("Mật khẩu phải có độ dài từ %d đến %d ký tự.", minLength, maxLength));
        }
    }

    /**
     * Xác thực rằng một chuỗi không rỗng hoặc chỉ chứa khoảng trắng.
     * @param value Chuỗi cần xác thực.
     * @param fieldName Tên trường để sử dụng trong thông báo lỗi.
     * @throws ValidationException nếu chuỗi rỗng hoặc chỉ chứa khoảng trắng.
     */
    public void validateNotBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " không được để trống.");
        }
    }

    /**
     * Xác thực rằng một đối tượng không phải là null.
     * @param object Đối tượng cần xác thực.
     * @param fieldName Tên trường để sử dụng trong thông báo lỗi.
     * @throws ValidationException nếu đối tượng là null.
     */
    public void validateNotNull(Object object, String fieldName) {
        if (object == null) {
            throw new ValidationException(fieldName + " không được là null.");
        }
    }

    /**
     * Xác thực rằng một giá trị số không âm.
     * @param value Giá trị số cần xác thực.
     * @param fieldName Tên trường để sử dụng trong thông báo lỗi.
     * @throws ValidationException nếu giá trị âm.
     */
    public void validateNonNegative(int value, String fieldName) {
        if (value < 0) {
            throw new ValidationException(fieldName + " không được âm.");
        }
    }
}