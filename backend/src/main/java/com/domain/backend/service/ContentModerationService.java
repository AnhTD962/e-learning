package com.domain.backend.service;

import com.domain.backend.dto.response.MessageResponse;
import com.domain.backend.entity.ContentModeration;
import com.domain.backend.exception.ResourceNotFoundException;
import com.domain.backend.exception.UnauthorizedException;
import com.domain.backend.exception.ValidationException;
import com.domain.backend.repository.ContentModerationRepository;
import com.domain.backend.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Dịch vụ xử lý logic nghiệp vụ liên quan đến kiểm duyệt nội dung.
 * Chỉ ADMIN mới có thể thực hiện các hành động kiểm duyệt.
 */
@Service
public class ContentModerationService {

    @Autowired
    private ContentModerationRepository contentModerationRepository;

    /**
     * Tạo một bản ghi kiểm duyệt nội dung mới.
     *
     * @param moderationLog Đối tượng ContentModeration cần tạo.
     * @return ContentModeration đã tạo.
     * @throws UnauthorizedException nếu người dùng không phải là ADMIN.
     * @throws ValidationException   nếu dữ liệu đầu vào không hợp lệ.
     */
    public ContentModeration createModerationLog(ContentModeration moderationLog) {
        if (!SecurityUtils.isAdmin()) {
            throw new UnauthorizedException("Bạn không được phép tạo bản ghi kiểm duyệt nội dung.");
        }
        if (moderationLog.getEntityType() == null || moderationLog.getEntityType().isEmpty() ||
                moderationLog.getEntityId() == null || moderationLog.getEntityId().isEmpty() ||
                moderationLog.getModerationAction() == null || moderationLog.getModerationAction().isEmpty()) {
            throw new ValidationException("Loại thực thể, ID thực thể và hành động kiểm duyệt không được để trống.");
        }
        moderationLog.setModeratedByUserId(SecurityUtils.getCurrentUserId()); // Tự động đặt người kiểm duyệt
        return contentModerationRepository.save(moderationLog);
    }

    /**
     * Lấy một bản ghi kiểm duyệt nội dung theo ID.
     *
     * @param id ID của bản ghi kiểm duyệt.
     * @return Optional chứa ContentModeration nếu tìm thấy.
     */
    public Optional<ContentModeration> getModerationLogById(String id) {
        if (!SecurityUtils.isAdmin()) {
            throw new UnauthorizedException("Bạn không được phép xem bản ghi kiểm duyệt nội dung.");
        }
        return contentModerationRepository.findById(id);
    }

    /**
     * Lấy tất cả các bản ghi kiểm duyệt nội dung.
     *
     * @return Danh sách ContentModeration.
     */
    public List<ContentModeration> getAllModerationLogs() {
        if (!SecurityUtils.isAdmin()) {
            throw new UnauthorizedException("Bạn không được phép xem tất cả bản ghi kiểm duyệt nội dung.");
        }
        return contentModerationRepository.findAll();
    }

    /**
     * Lấy các bản ghi kiểm duyệt nội dung theo loại thực thể và ID thực thể.
     *
     * @param entityType Loại thực thể.
     * @param entityId   ID thực thể.
     * @return Danh sách ContentModeration phù hợp.
     */
    public List<ContentModeration> getModerationLogsByEntity(String entityType, String entityId) {
        if (!SecurityUtils.isAdmin()) {
            throw new UnauthorizedException("Bạn không được phép xem bản ghi kiểm duyệt nội dung.");
        }
        return contentModerationRepository.findByEntityTypeAndEntityId(entityType, entityId);
    }

    /**
     * Cập nhật một bản ghi kiểm duyệt nội dung hiện có.
     *
     * @param id         ID của bản ghi cần cập nhật.
     * @param updatedLog Đối tượng ContentModeration với thông tin cập nhật.
     * @return ContentModeration đã cập nhật.
     * @throws ResourceNotFoundException nếu không tìm thấy bản ghi.
     * @throws UnauthorizedException     nếu người dùng không phải là ADMIN.
     */
    public ContentModeration updateModerationLog(String id, ContentModeration updatedLog) {
        if (!SecurityUtils.isAdmin()) {
            throw new UnauthorizedException("Bạn không được phép cập nhật bản ghi kiểm duyệt nội dung.");
        }
        ContentModeration existingLog = contentModerationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bản ghi kiểm duyệt", "id", id));

        existingLog.setModerationAction(updatedLog.getModerationAction());
        existingLog.setReason(updatedLog.getReason());
        existingLog.setModeratedByUserId(SecurityUtils.getCurrentUserId()); // Cập nhật người kiểm duyệt
        existingLog.setModerationDate(java.time.LocalDateTime.now()); // Cập nhật thời gian

        return contentModerationRepository.save(existingLog);
    }

    /**
     * Xóa một bản ghi kiểm duyệt nội dung.
     *
     * @param id ID của bản ghi cần xóa.
     * @return MessageResponse thành công.
     * @throws ResourceNotFoundException nếu không tìm thấy bản ghi.
     * @throws UnauthorizedException     nếu người dùng không phải là ADMIN.
     */
    public MessageResponse deleteModerationLog(String id) {
        if (!SecurityUtils.isAdmin()) {
            throw new UnauthorizedException("Bạn không được phép xóa bản ghi kiểm duyệt nội dung.");
        }
        ContentModeration existingLog = contentModerationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bản ghi kiểm duyệt", "id", id));
        contentModerationRepository.delete(existingLog);
        return new MessageResponse("Bản ghi kiểm duyệt đã xóa thành công!");
    }
}
