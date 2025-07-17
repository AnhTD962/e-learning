package com.domain.backend.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Lớp tiện ích cho các thao tác liên quan đến ngày và giờ.
 */
@Component
public class DateUtils {

    private static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT);

    /**
     * Định dạng LocalDateTime thành chuỗi theo định dạng mặc định.
     *
     * @param dateTime Đối tượng LocalDateTime.
     * @return Chuỗi ngày giờ đã định dạng.
     */
    public String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DEFAULT_FORMATTER);
    }

    /**
     * Phân tích cú pháp chuỗi ngày giờ thành LocalDateTime theo định dạng mặc định.
     *
     * @param dateTimeString Chuỗi ngày giờ.
     * @return Đối tượng LocalDateTime.
     * @throws DateTimeParseException nếu chuỗi không thể phân tích cú pháp.
     */
    public LocalDateTime parseDateTime(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.trim().isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeString, DEFAULT_FORMATTER);
    }

    /**
     * Định dạng LocalDateTime thành chuỗi theo định dạng tùy chỉnh.
     *
     * @param dateTime Đối tượng LocalDateTime.
     * @param pattern  Mẫu định dạng (ví dụ: "dd/MM/yyyy HH:mm").
     * @return Chuỗi ngày giờ đã định dạng.
     */
    public String formatDateTime(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * Phân tích cú pháp chuỗi ngày giờ thành LocalDateTime theo định dạng tùy chỉnh.
     *
     * @param dateTimeString Chuỗi ngày giờ.
     * @param pattern        Mẫu định dạng.
     * @return Đối tượng LocalDateTime.
     * @throws DateTimeParseException nếu chuỗi không thể phân tích cú pháp.
     */
    public LocalDateTime parseDateTime(String dateTimeString, String pattern) {
        if (dateTimeString == null || dateTimeString.trim().isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeString, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * Tính toán thời lượng giữa hai thời điểm bằng phút.
     *
     * @param start Thời điểm bắt đầu.
     * @param end   Thời điểm kết thúc.
     * @return Thời lượng tính bằng phút.
     */
    public long calculateDurationInMinutes(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 0;
        }
        return java.time.Duration.between(start, end).toMinutes();
    }
}