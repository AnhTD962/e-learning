package com.domain.backend.service;

import com.domain.backend.dto.response.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Dịch vụ để gửi email.
 * Yêu cầu cấu hình Spring Mail Sender trong application.properties.
 */
@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired(required = false) // Đặt là false vì có thể không phải lúc nào cũng cấu hình mail
    private JavaMailSender mailSender;

    /**
     * Gửi một email đơn giản.
     *
     * @param to      Địa chỉ email người nhận.
     * @param subject Chủ đề email.
     * @param text    Nội dung email.
     * @return MessageResponse thành công hoặc lỗi.
     */
    public MessageResponse sendSimpleEmail(String to, String subject, String text) {
        if (mailSender == null) {
            logger.warn("JavaMailSender không được cấu hình. Email sẽ không được gửi.");
            return new MessageResponse("Lỗi: Dịch vụ email chưa được cấu hình.");
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            // message.setFrom("noreply@elearning.com"); // Cấu hình trong application.properties

            mailSender.send(message);
            logger.info("Email đã gửi thành công tới: {}", to);
            return new MessageResponse("Email đã gửi thành công!");
        } catch (MailException e) {
            logger.error("Lỗi khi gửi email tới {}: {}", to, e.getMessage(), e);
            return new MessageResponse("Lỗi khi gửi email: " + e.getMessage());
        }
    }
}
