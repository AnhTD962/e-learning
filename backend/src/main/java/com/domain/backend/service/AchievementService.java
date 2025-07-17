package com.domain.backend.service;

import com.domain.backend.dto.response.AchievementResponse;
import com.domain.backend.dto.response.MessageResponse;
import com.domain.backend.entity.Achievement;
import com.domain.backend.entity.User;
import com.domain.backend.entity.UserProgress;
import com.domain.backend.exception.ResourceNotFoundException;
import com.domain.backend.exception.UnauthorizedException;
import com.domain.backend.exception.ValidationException;
import com.domain.backend.repository.AchievementRepository;
import com.domain.backend.repository.UserProgressRepository;
import com.domain.backend.repository.UserRepository;
import com.domain.backend.security.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Dịch vụ xử lý logic nghiệp vụ liên quan đến thành tích người dùng.
 * Bao gồm việc tạo, quản lý thành tích và kiểm tra điều kiện để trao thành tích.
 */
@Service
public class AchievementService {

    @Autowired
    private AchievementRepository achievementRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProgressRepository userProgressRepository;

    @Autowired
    private NotificationService notificationService; // Để thông báo khi đạt được thành tích

    /**
     * Chuyển đổi Achievement entity sang AchievementResponse DTO.
     *
     * @param achievement Entity Achievement.
     * @return AchievementResponse DTO.
     */
    private AchievementResponse convertToAchievementResponse(Achievement achievement) {
        AchievementResponse response = new AchievementResponse();
        BeanUtils.copyProperties(achievement, response);
        return response;
    }

    /**
     * Tạo một thành tích mới.
     * Chỉ ADMIN mới có thể tạo.
     *
     * @param achievement Đối tượng Achievement cần tạo.
     * @return AchievementResponse đã tạo.
     * @throws UnauthorizedException nếu người dùng không phải là ADMIN.
     * @throws ValidationException   nếu tên thành tích đã tồn tại.
     */
    public AchievementResponse createAchievement(Achievement achievement) {
        if (!SecurityUtils.isAdmin()) {
            throw new UnauthorizedException("Bạn không được phép tạo thành tích.");
        }
        if (achievementRepository.findByName(achievement.getName()).isPresent()) { // Giả định có findByName
            throw new ValidationException("Thành tích với tên '" + achievement.getName() + "' đã tồn tại.");
        }
        Achievement savedAchievement = achievementRepository.save(achievement);
        return convertToAchievementResponse(savedAchievement);
    }

    /**
     * Lấy một thành tích theo ID.
     *
     * @param id ID của thành tích.
     * @return Optional chứa AchievementResponse nếu tìm thấy.
     */
    public Optional<AchievementResponse> getAchievementById(String id) {
        return achievementRepository.findById(id)
                .map(this::convertToAchievementResponse);
    }

    /**
     * Lấy tất cả các thành tích.
     *
     * @return Danh sách AchievementResponse.
     */
    public List<AchievementResponse> getAllAchievements() {
        return achievementRepository.findAll().stream()
                .map(this::convertToAchievementResponse)
                .collect(Collectors.toList());
    }

    /**
     * Cập nhật một thành tích hiện có.
     * Chỉ ADMIN mới có thể cập nhật.
     *
     * @param id                 ID của thành tích cần cập nhật.
     * @param updatedAchievement Đối tượng Achievement với thông tin cập nhật.
     * @return AchievementResponse đã cập nhật.
     * @throws ResourceNotFoundException nếu không tìm thấy thành tích.
     * @throws UnauthorizedException     nếu người dùng không phải là ADMIN.
     */
    public AchievementResponse updateAchievement(String id, Achievement updatedAchievement) {
        if (!SecurityUtils.isAdmin()) {
            throw new UnauthorizedException("Bạn không được phép cập nhật thành tích.");
        }
        Achievement existingAchievement = achievementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Thành tích", "id", id));

        existingAchievement.setName(updatedAchievement.getName());
        existingAchievement.setDescription(updatedAchievement.getDescription());
        existingAchievement.setIconUrl(updatedAchievement.getIconUrl());
        existingAchievement.setCriteria(updatedAchievement.getCriteria());
        existingAchievement.setType(updatedAchievement.getType());
        existingAchievement.setUpdatedAt();

        Achievement savedAchievement = achievementRepository.save(existingAchievement);
        return convertToAchievementResponse(savedAchievement);
    }

    /**
     * Xóa một thành tích.
     * Chỉ ADMIN mới có thể xóa.
     *
     * @param id ID của thành tích cần xóa.
     * @return MessageResponse thành công.
     * @throws ResourceNotFoundException nếu không tìm thấy thành tích.
     * @throws UnauthorizedException     nếu người dùng không phải là ADMIN.
     */
    public MessageResponse deleteAchievement(String id) {
        if (!SecurityUtils.isAdmin()) {
            throw new UnauthorizedException("Bạn không được phép xóa thành tích.");
        }
        Achievement existingAchievement = achievementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Thành tích", "id", id));
        achievementRepository.delete(existingAchievement);
        return new MessageResponse("Thành tích đã xóa thành công!");
    }

    /**
     * Trao một thành tích cho người dùng.
     * Thường được gọi nội bộ khi người dùng đáp ứng tiêu chí.
     *
     * @param userId        ID của người dùng.
     * @param achievementId ID của thành tích.
     * @return MessageResponse thành công hoặc lỗi.
     * @throws ResourceNotFoundException nếu người dùng hoặc thành tích không tìm thấy.
     * @throws ValidationException       nếu thành tích đã được trao cho người dùng.
     */
    public MessageResponse grantAchievementToUser(String userId, String achievementId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "id", userId));
        Achievement achievement = achievementRepository.findById(achievementId)
                .orElseThrow(() -> new ResourceNotFoundException("Thành tích", "id", achievementId));

        // Giả định User entity có một Set<String> grantedAchievementIds
        if (user.getGrantedAchievementIds() == null) {
            user.setGrantedAchievementIds(new java.util.HashSet<>());
        }
        if (user.getGrantedAchievementIds().contains(achievementId)) {
            throw new ValidationException("Thành tích '" + achievement.getName() + "' đã được trao cho người dùng này.");
        }

        user.getGrantedAchievementIds().add(achievementId);
        userRepository.save(user);

        // Gửi thông báo cho người dùng
        notificationService.sendNotificationToUser(userId,
                "Chúc mừng! Bạn đã đạt được thành tích mới!",
                "Bạn đã đạt được thành tích: " + achievement.getName() + " - " + achievement.getDescription());

        return new MessageResponse("Thành tích '" + achievement.getName() + "' đã được trao cho người dùng " + user.getUsername());
    }

    /**
     * Kiểm tra và trao thành tích dựa trên tiến độ của người dùng.
     * Phương thức này có thể được gọi sau khi một bài học/khóa học được hoàn thành,
     * hoặc sau khi nộp quiz.
     *
     * @param userId ID của người dùng.
     */
    public void checkAndGrantAchievements(String userId) {
        // Lấy tất cả các thành tích có sẵn
        List<Achievement> allAchievements = achievementRepository.findAll();

        // Lấy tiến độ của người dùng
        List<UserProgress> userProgressList = userProgressRepository.findByUserId(userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "id", userId));

        // Giả định User entity có một Set<String> grantedAchievementIds
        if (user.getGrantedAchievementIds() == null) {
            user.setGrantedAchievementIds(new java.util.HashSet<>());
        }

        for (Achievement achievement : allAchievements) {
            // Chỉ kiểm tra nếu thành tích chưa được trao
            if (!user.getGrantedAchievementIds().contains(achievement.getId())) {
                boolean criteriaMet = false;
                switch (achievement.getType()) {
                    case "COURSE_COMPLETION":
                        // Ví dụ: Hoàn thành 1 khóa học
                        if (achievement.getCriteria().equals("1_COURSE_COMPLETED")) {
                            criteriaMet = userProgressList.stream().anyMatch(up -> "COMPLETED".equals(up.getStatus()));
                        }
                        // Ví dụ: Hoàn thành 5 khóa học
                        if (achievement.getCriteria().equals("5_COURSES_COMPLETED")) {
                            long completedCourses = userProgressList.stream().filter(up -> "COMPLETED".equals(up.getStatus())).count();
                            criteriaMet = completedCourses >= 5;
                        }
                        break;
                    case "QUIZ_MASTER":
                        // Ví dụ: Đạt 100% điểm trong 3 quiz
                        // Bạn sẽ cần truy vấn QuizAttemptRepository ở đây
                        // long perfectQuizzes = quizAttemptRepository.findByUserId(userId).stream()
                        //     .filter(qa -> qa.getPercentageScore() >= 100.0)
                        //     .count();
                        // criteriaMet = perfectQuizzes >= 3;
                        break;
                    case "VOCAB_PRO":
                        // Ví dụ: Học 100 từ mới
                        // Bạn sẽ cần một cách để theo dõi từ vựng đã học
                        break;
                    // Thêm các loại thành tích khác
                }

                if (criteriaMet) {
                    grantAchievementToUser(userId, achievement.getId());
                }
            }
        }
    }
}
