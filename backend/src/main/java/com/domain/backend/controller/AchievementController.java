package com.domain.backend.controller;

import com.domain.backend.dto.response.AchievementResponse;
import com.domain.backend.dto.response.MessageResponse;
import com.domain.backend.entity.Achievement;
import com.domain.backend.service.AchievementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/achievements") // Đường dẫn cơ sở cho các endpoint liên quan đến thành tích
public class AchievementController {

    @Autowired
    private AchievementService achievementService;

    /**
     * Tạo một thành tích mới.
     * Chỉ ADMIN mới có thể truy cập.
     *
     * @param achievement Đối tượng Achievement cần tạo.
     * @return ResponseEntity với AchievementResponse đã tạo.
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<AchievementResponse> createAchievement(@Valid @RequestBody Achievement achievement) {
        AchievementResponse createdAchievement = achievementService.createAchievement(achievement);
        return new ResponseEntity<>(createdAchievement, HttpStatus.CREATED);
    }

    /**
     * Lấy một thành tích theo ID.
     * Có thể truy cập bởi bất kỳ người dùng đã xác thực nào.
     *
     * @param id ID của thành tích.
     * @return ResponseEntity với AchievementResponse nếu tìm thấy, hoặc 404 Not Found.
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AchievementResponse> getAchievementById(@PathVariable String id) {
        return achievementService.getAchievementById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Lấy tất cả các thành tích.
     * Có thể truy cập bởi bất kỳ người dùng đã xác thực nào.
     *
     * @return ResponseEntity với danh sách AchievementResponse.
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AchievementResponse>> getAllAchievements() {
        List<AchievementResponse> achievements = achievementService.getAllAchievements();
        return ResponseEntity.ok(achievements);
    }

    /**
     * Cập nhật một thành tích hiện có.
     * Chỉ ADMIN mới có thể truy cập.
     *
     * @param id ID của thành tích cần cập nhật.
     * @param achievement Đối tượng Achievement với thông tin cập nhật.
     * @return ResponseEntity với AchievementResponse đã cập nhật.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<AchievementResponse> updateAchievement(@PathVariable String id, @Valid @RequestBody Achievement achievement) {
        AchievementResponse updatedAchievement = achievementService.updateAchievement(id, achievement);
        return ResponseEntity.ok(updatedAchievement);
    }

    /**
     * Xóa một thành tích.
     * Chỉ ADMIN mới có thể truy cập.
     *
     * @param id ID của thành tích cần xóa.
     * @return ResponseEntity với MessageResponse thành công.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<MessageResponse> deleteAchievement(@PathVariable String id) {
        MessageResponse response = achievementService.deleteAchievement(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Trao một thành tích cho người dùng.
     * Chỉ ADMIN mới có thể truy cập.
     * (Thường được gọi nội bộ bởi hệ thống, nhưng có thể có endpoint thủ công cho ADMIN)
     *
     * @param userId ID của người dùng.
     * @param achievementId ID của thành tích.
     * @return ResponseEntity với MessageResponse thành công.
     */
    @PostMapping("/{achievementId}/grant-to-user/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<MessageResponse> grantAchievementToUser(@PathVariable String userId, @PathVariable String achievementId) {
        MessageResponse response = achievementService.grantAchievementToUser(userId, achievementId);
        return ResponseEntity.ok(response);
    }

    /**
     * Kiểm tra và trao thành tích cho người dùng hiện tại (kích hoạt thủ công).
     * Có thể truy cập bởi bất kỳ người dùng đã xác thực nào.
     * (Trong thực tế, điều này thường được kích hoạt tự động bởi các sự kiện hệ thống)
     *
     * @return ResponseEntity với MessageResponse.
     */
    @PostMapping("/check-my-achievements")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageResponse> checkMyAchievements() {
        // Lấy ID người dùng hiện tại từ SecurityContextHolder
        String currentUserId = com.domain.backend.security.SecurityUtils.getCurrentUserId();
        achievementService.checkAndGrantAchievements(currentUserId);
        return ResponseEntity.ok(new MessageResponse("Đã kiểm tra thành tích của bạn."));
    }
}
