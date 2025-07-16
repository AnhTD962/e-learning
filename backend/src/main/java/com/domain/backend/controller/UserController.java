package com.domain.backend.controller;

import com.domain.backend.dto.request.UserProfileUpdateRequest;
import com.domain.backend.dto.response.MessageResponse;
import com.domain.backend.dto.response.UserResponse;
import com.domain.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users") // Đường dẫn cơ sở cho các endpoint liên quan đến người dùng
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Lấy thông tin người dùng theo ID.
     * Chỉ ADMIN hoặc người dùng đó mới có thể truy cập.
     *
     * @param id ID của người dùng.
     * @return ResponseEntity với UserResponse.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id) {
        UserResponse userResponse = userService.getUserById(id);
        return ResponseEntity.ok(userResponse);
    }

    /**
     * Lấy thông tin người dùng đã đăng nhập hiện tại.
     * Có thể truy cập bởi bất kỳ người dùng đã xác thực nào.
     *
     * @return ResponseEntity với UserResponse của người dùng hiện tại.
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> getCurrentUser() {
        UserResponse userResponse = userService.getCurrentUser();
        return ResponseEntity.ok(userResponse);
    }

    /**
     * Cập nhật hồ sơ người dùng.
     * Chỉ ADMIN hoặc người dùng đó mới có thể cập nhật.
     *
     * @param id ID của người dùng cần cập nhật.
     * @param updateRequest DTO chứa thông tin cập nhật.
     * @return ResponseEntity với UserResponse đã cập nhật.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<UserResponse> updateUserProfile(@PathVariable String id, @Valid @RequestBody UserProfileUpdateRequest updateRequest) {
        UserResponse updatedUser = userService.updateUserProfile(id, updateRequest);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Xóa người dùng theo ID.
     * Chỉ ADMIN mới có thể xóa người dùng.
     *
     * @param id ID của người dùng cần xóa.
     * @return ResponseEntity với MessageResponse thành công.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<MessageResponse> deleteUser(@PathVariable String id) {
        MessageResponse response = userService.deleteUser(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Lấy tất cả người dùng.
     * Chỉ ADMIN mới có thể truy cập.
     *
     * @return ResponseEntity với danh sách UserResponse.
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}
