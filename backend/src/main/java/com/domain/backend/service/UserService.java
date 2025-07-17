package com.domain.backend.service;

import com.domain.backend.dto.request.UserProfileUpdateRequest;
import com.domain.backend.dto.response.MessageResponse;
import com.domain.backend.dto.response.UserResponse;
import com.domain.backend.entity.User;
import com.domain.backend.exception.ResourceNotFoundException;
import com.domain.backend.exception.UnauthorizedException;
import com.domain.backend.exception.ValidationException;
import com.domain.backend.repository.UserRepository;
import com.domain.backend.security.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Dịch vụ xử lý logic nghiệp vụ liên quan đến người dùng (ngoài xác thực).
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Để mã hóa mật khẩu nếu cập nhật

    /**
     * Lấy thông tin người dùng theo ID.
     *
     * @param id ID của người dùng.
     * @return UserResponse chứa thông tin người dùng.
     * @throws ResourceNotFoundException nếu không tìm thấy người dùng.
     */
    public UserResponse getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "id", id));
        return convertToUserResponse(user);
    }

    /**
     * Lấy thông tin người dùng đã đăng nhập hiện tại.
     *
     * @return UserResponse chứa thông tin người dùng hiện tại.
     * @throws UnauthorizedException     nếu không có người dùng nào được xác thực.
     * @throws ResourceNotFoundException nếu người dùng được xác thực không tồn tại trong DB.
     */
    public UserResponse getCurrentUser() {
        String userId = SecurityUtils.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "id", userId));
        return convertToUserResponse(user);
    }

    /**
     * Cập nhật hồ sơ người dùng.
     * Chỉ người dùng đó hoặc ADMIN mới có thể cập nhật hồ sơ.
     *
     * @param id            ID của người dùng cần cập nhật.
     * @param updateRequest DTO chứa thông tin cập nhật.
     * @return UserResponse của người dùng đã cập nhật.
     * @throws ResourceNotFoundException nếu không tìm thấy người dùng.
     * @throws UnauthorizedException     nếu người dùng không được phép cập nhật hồ sơ này.
     * @throws ValidationException       nếu email đã được sử dụng.
     */
    public UserResponse updateUserProfile(String id, UserProfileUpdateRequest updateRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "id", id));

        // Kiểm tra ủy quyền: chỉ người dùng đó hoặc ADMIN mới có thể cập nhật
        if (!SecurityUtils.getCurrentUserId().equals(id) && !SecurityUtils.isAdmin()) {
            throw new UnauthorizedException("Bạn không được phép cập nhật hồ sơ này.");
        }

        // Cập nhật username nếu được cung cấp
        if (updateRequest.getUsername() != null && !updateRequest.getUsername().trim().isEmpty()) {
            user.setUsername(updateRequest.getUsername());
        }

        // Cập nhật email nếu được cung cấp và khác với email hiện tại
        if (updateRequest.getEmail() != null && !updateRequest.getEmail().trim().isEmpty() && !updateRequest.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(updateRequest.getEmail())) {
                throw new ValidationException("Lỗi: Email đã được sử dụng!");
            }
            user.setEmail(updateRequest.getEmail());
        }

        if (updateRequest.getProfilePictureUrl() != null) {
            user.setProfileImage(updateRequest.getProfilePictureUrl()); // Giả định trường 'profilePictureUrl' tồn tại
        }

        user.setUpdatedAt();
        User updatedUser = userRepository.save(user);
        return convertToUserResponse(updatedUser);
    }

    /**
     * Xóa người dùng theo ID.
     * Chỉ ADMIN mới có thể xóa người dùng.
     *
     * @param id ID của người dùng cần xóa.
     * @return MessageResponse thành công.
     * @throws ResourceNotFoundException nếu không tìm thấy người dùng.
     * @throws UnauthorizedException     nếu người dùng không phải là ADMIN.
     */
    public MessageResponse deleteUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "id", id));

        if (!SecurityUtils.isAdmin()) {
            throw new UnauthorizedException("Bạn không được phép xóa người dùng.");
        }

        userRepository.delete(user);
        return new MessageResponse("Người dùng đã xóa thành công!");
    }

    /**
     * Lấy tất cả người dùng (chỉ dành cho ADMIN).
     *
     * @return Danh sách UserResponse.
     */
    public List<UserResponse> getAllUsers() {
        if (!SecurityUtils.isAdmin()) {
            throw new UnauthorizedException("Bạn không được phép xem danh sách người dùng.");
        }
        return userRepository.findAll().stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
    }

    /**
     * Chuyển đổi User entity sang UserResponse DTO.
     *
     * @param user Entity User.
     * @return UserResponse DTO.
     */
    private UserResponse convertToUserResponse(User user) {
        UserResponse userResponse = new UserResponse();
        BeanUtils.copyProperties(user, userResponse);
        // Đảm bảo không sao chép passwordHash
        userResponse.setRoles(user.getRoles()); // Đảm bảo vai trò được sao chép
        return userResponse;
    }
}
