package com.domain.backend.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "contentModerationLogs")
public class ContentModeration {

    @Id
    private String id;

    private String entityType; // Loại thực thể được kiểm duyệt (ví dụ: "LESSON", "COMMENT", "USER_PROFILE")
    private String entityId;   // ID của thực thể được kiểm duyệt
    private String reportedByUserId; // ID của người dùng báo cáo (tùy chọn)
    private String moderatedByUserId; // ID của quản trị viên đã thực hiện kiểm duyệt
    private String moderationAction; // Hành động kiểm duyệt (ví dụ: "REVIEWED", "BLOCKED", "DELETED", "APPROVED")
    private String reason; // Lý do kiểm duyệt
    private LocalDateTime moderationDate;

    public ContentModeration() {
        this.moderationDate = LocalDateTime.now();
    }
}