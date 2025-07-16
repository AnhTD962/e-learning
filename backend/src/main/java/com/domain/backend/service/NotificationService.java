package com.domain.backend.service;

import com.domain.backend.dto.response.MessageResponse;
import com.domain.backend.entity.User;
import com.domain.backend.exception.ResourceNotFoundException;
import com.domain.backend.exception.UnauthorizedException;
import com.domain.backend.repository.UserRepository;
import com.domain.backend.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Dịch vụ xử lý việc gửi thông báo cho người dùng.
 * Có thể mở rộng để tích hợp với WebSocket hoặc các hệ thống thông báo khác.
 */
@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private EmailService emailService; // Để gửi thông báo qua email

    @Autowired
    private UserRepository userRepository; // Để lấy thông tin người dùng

    // Giả định một WebSocketService sẽ được thêm vào sau này để gửi thông báo real-time
    // @Autowired
    // private WebSocketService webSocketService;

    /**
     * Gửi thông báo cho một người dùng cụ thể.
     *
     * @param userId ID của người dùng nhận thông báo.
     * @param subject Chủ đề thông báo.
     * @param message Nội dung thông báo.
     * @return MessageResponse thành công hoặc lỗi.
     */
    public MessageResponse sendNotificationToUser(String userId, String subject, String message) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("Người dùng", "id", userId);
        }
        User user = userOptional.get();

        // Gửi qua email
        MessageResponse emailResult = emailService.sendSimpleEmail(user.getEmail(), subject, message);
        if (!emailResult.getMessage().contains("thành công")) {
            logger.warn("Không thể gửi email thông báo tới {}: {}", user.getEmail(), emailResult.getMessage());
        }

        // Gửi qua WebSocket (nếu được triển khai)
        // if (webSocketService != null) {
        //     webSocketService.sendNotification(userId, subject, message);
        // }

        logger.info("Thông báo đã gửi tới người dùng {}: {}", userId, subject);
        return new MessageResponse("Thông báo đã gửi thành công tới người dùng " + user.getUsername());
    }

    /**
     * Gửi thông báo cho tất cả người dùng (chỉ ADMIN).
     *
     * @param subject Chủ đề thông báo.
     * @param message Nội dung thông báo.
     * @return MessageResponse thành công hoặc lỗi.
     */
    public MessageResponse sendNotificationToAllUsers(String subject, String message) {
        if (!SecurityUtils.isAdmin()) {
            throw new UnauthorizedException("Bạn không được phép gửi thông báo cho tất cả người dùng.");
        }

        List<User> allUsers = userRepository.findAll();
        int successCount = 0;
        for (User user : allUsers) {
            MessageResponse result = emailService.sendSimpleEmail(user.getEmail(), subject, message);
            if (result.getMessage().contains("thành công")) {
                successCount++;
            } else {
                logger.warn("Không thể gửi thông báo tới {}: {}", user.getEmail(), result.getMessage());
            }
            // Gửi qua WebSocket cho từng người dùng (nếu được triển khai)
            // if (webSocketService != null) {
            //     webSocketService.sendNotification(user.getId(), subject, message);
            // }
        }
        logger.info("Thông báo đã gửi tới {}/{} người dùng.", successCount, allUsers.size());
        return new MessageResponse(String.format("Thông báo đã gửi thành công tới %d/%d người dùng.", successCount, allUsers.size()));
    }
}
